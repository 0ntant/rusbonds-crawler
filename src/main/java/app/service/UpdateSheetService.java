package app.service;

import app.exception.InvalidIsinException;
import app.integration.docker.ClientChromeDocker;
import app.mapper.AccountingPolicyMapper;
import app.mapper.BondMapper;
import app.model.AccountingPolicy;
import app.model.Bond;
import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Slf4j
public class UpdateSheetService
{
    RusbondsService rusbondsServ;
    BondSheetService bondSheetServ;
    SelRusbondsServiceProxy selRusbondsSer;
    ClientChromeDocker clientChromeDocker;
    List<AccountingPolicy> accPolices;

    public UpdateSheetService()
    {
        GoogleSheetService googleSheetService = new GoogleSheetService();
        AccountingPolicySheetService accServ = new AccountingPolicySheetService(googleSheetService);

        bondSheetServ = new BondSheetService(googleSheetService);
        accPolices = accServ.getAll();
        clientChromeDocker = new ClientChromeDocker();
        rusbondsServ = new RusbondsService();
        selRusbondsSer = new SelRusbondsServiceProxy();
    }

    public void startUpdateCycle()
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
            int waitMinutesPerUpdates=rand.nextInt(5, 30);
            log.warn(
                    "Wait minutes={} before continue update, bonds update process interrupt = {}",
                    waitMinutesPerUpdates,
                    ex.getMessage()
            );
            selRusbondsSer.closeSession();
            waitMinutes(waitMinutesPerUpdates);
            startUpdateCycle();
        }

        selRusbondsSer.closeSession();
        selRusbondsSer.stopProxy();

        List<Bond> bonds = bondSheetServ.getAll();
        bondSheetServ.writeBonds(bonds);
    }

    private void prepareKeys()
    {
        selRusbondsSer.createAuthSession();
        rusbondsServ = new RusbondsService(selRusbondsSer.getRusbondsCred());
    }

    public void updateProcess()
    {
        rusbondsServ.login();
        if (!isNeedUpdate())
        {
            log.warn("Update not needed");
            return;
        }
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

        if (localDate.getDayOfWeek().equals(DayOfWeek.SATURDAY)
            || localDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)
        )
        {
            log.warn("Today is {}", localDate.getDayOfWeek());
            return false;
        }

        if (localDate.isEqual(bondSheetServ.getModifyDate()))
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
            return;
        }

        updateMarketValueNow(bond, findtoolId);
        updateRating(bond, findtoolId);
        updateAction(bond);

        saveUpdateBond(bond);
    }

    public List<Bond> getBondsToUpdate()
    {
        List<Bond> bondsToUpdate = new ArrayList<>();
        for(Bond bond : bondSheetServ.getAll())
        {
            if (bondNeedUpdate(bond))
            {
               bondsToUpdate.add(bond);
            }
        }
        return  bondsToUpdate;
    }

    private boolean bondNeedUpdate(Bond bond)
    {
        if (bond.getIsin().isEmpty())
        {
            return false;
        }

        if (bond.getSysModifyDate().isEqual(LocalDate.now()))
        {
            return false;
        }
        return true;
    }

    public void saveUpdateBond(Bond bond)
    {
        log.info("Save updated bond ISIN={}", bond.getIsin());
        bond.setSysModifyDate(LocalDate.now());
        bondSheetServ.writeBond(bond);
    }

    private void saveUpdate()
    {
        log.info("Bonds successfully updated");
        bondSheetServ.exportData();
        bondSheetServ.writeModifyDate();
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

        if (BondMapper.isRatingMultiple(bond.getRating()))
        {
            log.info("Bond ISIN={} has a multiple rating", bond.getIsin());
            pureRating = getLowestRating(BondMapper.getPureRatings(bond.getRating()));
        }
        else
        {
            pureRating = BondMapper.pureRating(bond.getRating());
        }

        int newAction = getAccRatingLotCount(pureRating) - bond.getCount();

        if (newAction != bond.getAction())
        {
            log.info("Bond ISIN={} last action={} new action={}",
                    bond.getIsin(),
                    bond.getAction(),
                    newAction
            );

            bond.setAction(newAction);
        }
    }

//    private boolean isRatingMultiple(String rating)
//    {
//        return getPureRatings(rating).size() > 1;
//    }
//
//    private List<String> getPureRatings(String ratings)
//    {
//        return Arrays
//                .stream(ratings.split("/"))
//                .map(BondMapper::pureRating)
//                .toList();
//    }

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

    private void waitMinutes(int minutes)
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
