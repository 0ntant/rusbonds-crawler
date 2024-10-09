package app.service;

import app.exception.InvalidIsinException;
import app.integration.docker.ClientChromeDocker;
import app.model.Bond;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
public class UpdateSheetService
{
    RusbondsService rusbondsServ;
    BondSheetService bondSheetServ;
    SelRusbondsServiceProxy selRusbondsSer;
    ClientChromeDocker clientChromeDocker;

    int waitMinutesPerUpdates = 0;

    public UpdateSheetService()
    {
        clientChromeDocker = new ClientChromeDocker();
        bondSheetServ = new BondSheetService();
        rusbondsServ = new RusbondsService();
        selRusbondsSer = new SelRusbondsServiceProxy();
    }

    public void startUpdate()
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
            waitMinutesPerUpdates=rand.nextInt(15, 40);
            log.warn(
                    "Bonds update process interrupt = {}, wait minutes={} before continue update",
                    ex.getMessage(),
                    waitMinutesPerUpdates
            );
            waitMinutes(waitMinutesPerUpdates);
            startUpdate();
        }
        finally
        {
            selRusbondsSer.closeSession();
        }
        selRusbondsSer.stopProxy();

        List<Bond> bonds = bondSheetServ.getAll();
        bondSheetServ.writeBonds(bonds);
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

    private void prepareKeys()
    {
        selRusbondsSer.createAuthSession();
        rusbondsServ = new RusbondsService(selRusbondsSer.getRusbondsCred());
    }

    public boolean isNeedUpdate()
    {
        LocalDate localDate = LocalDate.now();
        return !localDate.isEqual(bondSheetServ.getModifyDate());
    }

    private void updateBond(Bond bond)
    {
        int findtoolId = 0;
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
        int issuerId = rusbondsServ.getIssuerId(findtoolId);
        updateMarketValueNow(bond, findtoolId);
        updateRating(bond, findtoolId);
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

    private void updateRating(Bond bond, int findtoolId)
    {
      //  String newRating = rusbondsServ.getRating(issuerId);
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
