package services;

import com.fdflib.model.entity.FdfEntity;
import com.fdflib.service.GenericService;
import models.Driver;

/**
 * Example FdfCommonServices (implemented by GenericService)
 * You can create wrapper service methods here if you wish or create custom ones
 *
 */
public class DriverService extends GenericService {

    /**
     * Example wrapper for FdfCommonServices.save
     * @param driver driver to save
     * @return saved driver
     */
    public Driver saveDriver(Driver driver) {
        if(driver != null) {
            return this.save(Driver.class, driver).current;
        }
        return null;
    }

    /**
     * Returns driver by id without history
     * @param id of driver to retrieve
     * @return Driver without history
     */
    public Driver getDriverById(long id) {
        return getDriverWithHistoryById(id).current;

    }

    /**
     * Example wrapper for FdfCommonServices.getEntityById that returns full FdfEntity including history
     * @param id of driver to retrieve
     * @return FdfEnitty<Driver> with history
     */
    public FdfEntity<Driver> getDriverWithHistoryById(long id) {
        FdfEntity<Driver> driver = new FdfEntity<>();

        // get the test
        if(id >= 0) {
            driver = this.getEntityById(Driver.class, id);
        }

        return driver;
    }

}
