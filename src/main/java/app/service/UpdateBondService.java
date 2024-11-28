package app.service;

import app.exception.InvalidIsinException;
import app.mapper.BondMapper;
import app.model.AccountingPolicy;
import app.model.Bond;
import app.model.BondRepayment;
import app.model.Env;
import app.util.NotificationMessageUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@AllArgsConstructor
@Builder
public class UpdateBondService
{
    RusbondsService rusbondsServ;
    BondService bondServ;
    SelRusbondsServiceProxy selRusbondsSer;
    List<AccountingPolicy> accPolices;
    AccountingPolicySheetService accServ;
    NotificationService notificationService;
    BondValidatorService bondValidatorService;

    public UpdateBondService()
    {
        GoogleSheetService googleSheetService = new GoogleSheetService();
        rusbondsServ = new RusbondsServiceImp();
        selRusbondsSer = new SelRusbondsServiceProxyImp();
        bondServ = new BondSheetService(googleSheetService);
        accServ = new AccountingPolicySheetService(googleSheetService);
        notificationService = new NotificationService(Env.PROD);
        bondValidatorService = new BondValidatorService();
    }

    public void startUpdateProcess()
    {
        Set<Bond> duplicates = bondValidatorService.getDuplicatesIsins(getBondsToUpdate());

        if (!duplicates.isEmpty())
        {
            notifyDuplicate(duplicates);

            log.error("Update not started because of duplicates={}",
                    duplicates
                            .stream()
                            .map(bond -> "num: %s isin %s".
                                    formatted(
                                            bond.getNumber(),
                                            bond.getIsin()
                                    )
                            )
                            .toList()
            );
            return;
        }

        if (!isNeedUpdate())
        {
            log.warn("Update not needed");
            selRusbondsSer.stopProxy();
            return;
        }
        prepareUpdateProcess();
        log.info("Start update process");
        startUpdateCycle();
    }


    private void prepareUpdateProcess()
    {
        log.info("Prepared data before update");
        accPolices = accServ.getAll();
    }

    private void startUpdateCycle()
    {
        Random rand = new Random();
        try
        {
            prepareKeys();
            updateProcess();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            selRusbondsSer.closeSession();
            int waitMinutesPerUpdates=rand.nextInt(5, 15);
            log.warn(
                    "Wait minutes={} before continue update, bonds update process interrupt: {}",
                    waitMinutesPerUpdates,
                    ex.getMessage()
            );
            selRusbondsSer.closeSession();
            waitMinutes(waitMinutesPerUpdates);
            startUpdateCycle();
        }

        selRusbondsSer.closeSession();
        selRusbondsSer.stopProxy();
    }

    private void prepareKeys()
    {
        selRusbondsSer.createAuthSession();
        rusbondsServ.setRusbondsKeys(selRusbondsSer.getRusbondsCred());
    }

    public void updateProcess()
    {
        rusbondsServ.login();

        List<Bond> bondsToUpdate = getBondsToUpdate();
        log.info("Bonds to update={}", bondsToUpdate.size());
        for(Bond bond : bondsToUpdate)
        {
            updateBond(bond);
            log.info("Bond number={} ISIN={} updated",
                    bondsToUpdate.indexOf(bond),
                    bond.getIsin()
            );
        }
        saveUpdate();
    }

    public boolean isNeedUpdate()
    {
        LocalDate localDate = LocalDate.now();

        if (localDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)
            || localDate.getDayOfWeek().equals(DayOfWeek.MONDAY)
        )
        {
            log.warn("Today is {}", localDate.getDayOfWeek());
            return false;
        }

        if (localDate.isEqual(bondServ.getModifyDate()))
        {
            log.warn("Bonds already updated today");
            return false;
        }

        return true;
    }

    private void updateBond(Bond bond)
    {
        int findtoolId;
        try
        {
            findtoolId = rusbondsServ.getFintoolId(bond);
        }
        catch (InvalidIsinException ex)
        {
            log.warn("Bond isin invalid={} set empty string",
                    bond.getIsin()
            );
            bond.setIsin("");
            saveUpdateBond(bond);
            bondValidatorService.getInvalidBonds().add(bond);
            return;
        }

        updateMarketValueNow(bond, findtoolId);
        updateRating(bond, findtoolId);
        updateActualBalanceCount(bond, findtoolId);
        updateAction(bond);
        updateCompTicketLiveTimeYear(bond);

        saveUpdateBond(bond);
    }

    public List<Bond> getBondsToUpdate()
    {
        List<Bond> bondsToUpdate = new ArrayList<>();
        for(Bond bond : bondServ.getBondsToUpdate())
        {
            if (bond.isNeedUpdate())
            {
               bondsToUpdate.add(bond);
            }
        }
        return  bondsToUpdate;
    }

    public void saveUpdateBond(Bond bond)
    {
        log.info("Save updated bond ISIN={}", bond.getIsin());
        bond.setSysModifyDate(LocalDate.now());
        bondServ.writeBond(bond);
    }

    private void saveUpdate()
    {
        log.info("Bonds successfully updated");
        bondServ.exportData();
        bondServ.writeModifyDate();

        List<Bond> bondsInvalidIsins = bondValidatorService.getInvalidBonds();
        if (!bondsInvalidIsins.isEmpty())
        {
            notifyInvalidIsins(bondsInvalidIsins);
        }
    }

    private void notifyInvalidIsins(List<Bond> bonds)
    {
        notificationService.broadcastMessage(
                NotificationMessageUtil.invalidIsin(
                        bondServ.getSheetName(),
                        bonds
                )
        );
    }

    private void notifyDuplicate(Set<Bond> bonds)
    {
        notificationService.broadcastMessage(
                NotificationMessageUtil.duplicateBonds(
                        bondServ.getSheetName(),
                        bonds.stream().toList()
                )
        );
    }

    private void updateActualBalanceCount(Bond bond, int findtoolId)
    {
        List<BondRepayment> repayments = rusbondsServ.getRepayments(findtoolId);
        double deltaPercent = 100;
        for (BondRepayment repayment : repayments)
        {
            if (repayment.getMtyDate().isBefore(LocalDate.now())
                    || repayment.getMtyDate().isEqual(LocalDate.now()))
            {
                deltaPercent-=repayment.getMtyPart();
            }
        }
        int newActualBalanceCount = (int) Math.round( (double) bond.getCount() / 100 * deltaPercent);

        if (bond.getActualBalanceCount() != newActualBalanceCount)
        {
            log.info("Bond ISIN={} last ActualBalanceCount={} new ActualBalanceCount={}",
                    bond.getIsin(),
                    bond.getActualBalanceCount(),
                    newActualBalanceCount
            );
            bond.setActualBalanceCount(newActualBalanceCount);
        }
    }

    private void updateCompTicketLiveTimeYear(Bond bond)
    {
        Double lastValue = bond.getTicketLiveTimeYear();
        bond.setCompTicketLiveTimeYear();
        Double newValue = bond.getTicketLiveTimeYear();

        if(lastValue != newValue)
        {
            log.info("Bond ISIN={} last TicketLiveTimeYear={} new TicketLiveTimeYear={}",
                    bond.getIsin(),
                    lastValue,
                    newValue
            );
        }
    }

    private void updateMarketValueNow(Bond bond, int issuerId)
    {
        double newMarketValueNow = rusbondsServ.getMarketValueNow(issuerId);
        if(newMarketValueNow != bond.getMarketValueNow())
        {
            log.info("Bond ISIN={} last MarketValueNow={} new MarketValueNow={}",
                    bond.getIsin(),
                    bond.getMarketValueNow(),
                    newMarketValueNow
            );
            bond.setMarketValueNow(newMarketValueNow);
        }
    }

    private void updateRating(Bond bond, int findtoolId)
    {
        String newRating = selRusbondsSer.getBondRating(findtoolId);

        if (newRating == null
                || newRating.contains("null")
                || newRating.isEmpty()
                || newRating.isBlank()
        )
        {
            log.info("Bond ISIN={} NO_RATING", bond.getIsin());
            newRating = "-";
        }

        if(!newRating.equals(bond.getRating()))
        {
            log.info("Bond ISIN={} last rating={} new rating={}",
                    bond.getIsin(),
                    bond.getRating(),
                    newRating
            );
            bond.setRating(newRating);
        }
    }

    private void updateAction(Bond bond)
    {
        String pureRating;
        String bondRating = bond.getRating();

        if (BondMapper.isRatingMultiple(bondRating))
        {
            log.info("Bond ISIN={} has a multiple rating", bond.getIsin());
            pureRating = getLowestRating(BondMapper.getPureRatings(bondRating));
        }
        else
        {
            pureRating = BondMapper.pureRating(bondRating);
        }

        String newAction = String.valueOf(getAccRatingLotCount(pureRating) - bond.getActualBalanceCount());

        if(pureRating.contains("C") || pureRating.contains("D"))
        {
            newAction = "Срочно продать";
        }

        if (!newAction.equals(bond.getAction()))
        {
            log.info("Bond ISIN={} last action={} new action={}",
                    bond.getIsin(),
                    bond.getAction(),
                    newAction
            );
            bond.setAction(newAction);
        }
    }

    public String getLowestRating(List<String> ratingList)
    {
        int indexOfMinRating = ratingList.stream()
                .map(rating -> accPolices
                        .stream()
                        .filter(accountingPolicy -> accountingPolicy.getCompanyRating().equals(rating))
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException(
                                "No such AccountingPolicy rating=%s".formatted(rating)
                                )
                        )
                )
                .map(accPolicy -> accPolices.indexOf(accPolicy))
                .min(Integer::compareTo)
                .orElseThrow(
                        () -> new NoSuchElementException("Can't find min rating")
                );

        return accPolices.get(indexOfMinRating).getCompanyRating();
    }

    public int getAccRatingLotCount(String companyRating)
    {
        return accPolices.stream()
                .filter(acc -> acc.getCompanyRating().equals(companyRating))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "No such AccountingPolicy=%s".formatted(companyRating))
                )
                .getLotCount();
    }

    public void waitMinutes(int minutes)
    {
        synchronized (Thread.currentThread())
        {
            try
            {
                Thread.currentThread().wait(1000 * 60 * minutes);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }
    }
}
