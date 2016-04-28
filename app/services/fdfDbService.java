package services;

import com.fdflib.model.entity.FdfEntity;
import com.fdflib.persistence.database.DatabaseUtil;
import com.fdflib.service.FdfServices;
import com.fdflib.util.FdfSettings;
import models.Car;
import models.CarMake;
import models.Driver;
import play.Logger;
import play.inject.ApplicationLifecycle;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by brian.gormanly on 3/23/16.
 */
@Singleton
public class fdfDbService {

    DriverService ds = new DriverService();
    CarService cs = new CarService();

    @Inject
    public fdfDbService(ApplicationLifecycle appLifecycle) {
        // This code is called when the application starts.
        Logger.info("4dfLib example:: 4dflib initialization starting..");

        // get the 4dflib settings singleton
        FdfSettings fdfSettings = FdfSettings.getInstance();

        // Create a array that will hold the classes that make up our 4df data model
        List<Class> myModel = new ArrayList<>();

        // set the database type and name and connection information
        //fdfSettings.PERSISTENCE = DatabaseUtil.DatabaseType.POSTGRES;
        //fdfSettings.DB_PROTOCOL = DatabaseUtil.DatabaseProtocol.JDBC_POSTGRES;
        fdfSettings.PERSISTENCE = DatabaseUtil.DatabaseType.MYSQL;
        fdfSettings.DB_PROTOCOL = DatabaseUtil.DatabaseProtocol.JDBC_MYSQL;
        fdfSettings.DB_ENCODING = DatabaseUtil.DatabaseEncoding.UTF8;
        fdfSettings.DB_NAME = "4dfplayexample";
        fdfSettings.DB_HOST = "localhost";
        fdfSettings.DB_USER = "4dfplayexample";
        fdfSettings.DB_PASSWORD = "4dfplayexamplepassword";

        // root user settings are only required for initial database creation.  Once the database is created you
        // should remove this information
        //fdfSettings.DB_ROOT_USER = "postgres"; //postgres:
        fdfSettings.DB_ROOT_USER = "root";
        fdfSettings.DB_ROOT_PASSWORD = "";

        // set the default Tenant information
        fdfSettings.DEFAULT_TENANT_NAME = "example-tenant";
        fdfSettings.DEFAULT_TENANT_DESRIPTION = "Default Tenant the 4df example";
        fdfSettings.DEFAULT_TENANT_IS_PRIMARY = true;
        fdfSettings.DEFAULT_TENANT_WEBSITE = "http://www.4dflib.com";

        // set the default System information
        fdfSettings.DEFAULT_SYSTEM_NAME = "4dflib";
        fdfSettings.DEFAULT_SYSTEM_DESCRIPTION = "4df play example system";

        // add model objects
        myModel.add(Driver.class);
        myModel.add(Car.class);

        // call the initialization of library!
        FdfServices.initializeFdfDataModel(myModel);

        // check to see if the default data has been loaded
        List<FdfEntity<Car>> allCarCheck = cs.getAll(Car.class);
        if(allCarCheck != null && allCarCheck.size() == 0) {
            insertSomeData();
        }

        // create some default data

        Logger.info("4dfLib example:: 4dflib initialization complete..");

        // When the application starts, register a stop hook with the
        // ApplicationLifecyle object. The code inside the stop hook wil
        // be run when the application stops.
        appLifecycle.addStopHook(() -> {
            Logger.info("4dfLib example:: 4dflib initialization stopping..");


            return CompletableFuture.completedFuture(null);
        });
    }


    private void insertSomeData() {
        Logger.info("4dfLib example:: Start create default data..");

        // create a few of drivers
        Driver sam = new Driver();
        sam.firstName = "Sam";
        sam.lastName = "Holden";
        sam.phoneNumber = "212-555-1212";
        ds.saveDriver(sam);

        Driver harry = new Driver();
        harry.firstName = "Sam";
        harry.lastName = "Smith";
        harry.phoneNumber = "212-555-1313";
        harry = ds.saveDriver(harry);

        Driver jack = new Driver();
        jack.firstName = "Jack";
        jack.lastName = "Johnson";
        jack.phoneNumber = "212-555-1414";
        jack = ds.saveDriver(jack);

        // create a couple of cars
        Car mufasa = new Car();
        mufasa.name = "Mufasa";
        mufasa.color = "Teal";
        mufasa.isInNeedOfRepair = false;
        mufasa.description = "Total awesomeness";
        mufasa.make = CarMake.PONTIAC;
        mufasa.model = "Trans Am";
        mufasa.year = 1983;
        cs.saveCar(mufasa);

        Car cv1 = new Car();
        cv1.name = "Medallion 1";
        cv1.color = "Yellow";
        cv1.isInNeedOfRepair = false;
        cv1.description = "NYC has the best taxis";
        cv1.make = CarMake.FORD;
        cv1.model = "LTD Crown Victoria";
        cv1.year = 2000;
        // assign jack to the this car
        cv1.currentDriverId = jack.id;
        cs.saveCar(cv1);

        // since we did not assign anyone to drive mufasa, lets do that now
        mufasa = cs.getCarsByName("Mufasa");
        mufasa.currentDriverId = harry.id;
        cs.saveCar(mufasa);

        Logger.info("4dfLib example:: End create default data..");

    }

}