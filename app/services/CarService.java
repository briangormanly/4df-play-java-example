package services;

import com.fdflib.model.entity.FdfEntity;
import com.fdflib.model.util.WhereClause;
import com.fdflib.persistence.FdfPersistence;
import com.fdflib.service.GenericService;
import models.Car;

import java.util.ArrayList;
import java.util.List;

/**
 * Example FdfCommonServices (implemented by GenericService) wrapper service
 * You can create wrapper service methods here if you wish or create custom ones,
 */
public class CarService extends GenericService {

    /**
     * Example wrapper for FdfCommonServices.save
     * Enforces name being a distinct member of the Entity.
     * @param car to save
     * @return saved car
     */
    public Car saveCar(Car car) {
        if(car != null) {
            // name is unique for car so we are going to check for existing one first
            Car existingCar = getCarsByName(car.name);
            if(existingCar != null) {
                // if a match is found, just set the id to match the existing one and it will automatically update
                // instead of insert.
                car.id = existingCar.id;
            }

            if (car != null) {
                return this.save(Car.class, car).current;
            }
        }
        return null;
    }

    /**
     * Returns car by id without history
     * @param id of car to retrieve
     * @return car without history
     */
    public Car getCarById(long id) {
        return getCarWithHistoryById(id).current;

    }

    /**
     * Example wrapper for FdfCommonServices.getEntityById that returns full FdfEntity including history
     * @param id of car to retrieve
     * @return FdfEnitty<Car> with history
     */
    public FdfEntity<Car> getCarWithHistoryById(long id) {
        FdfEntity<Car> car = new FdfEntity<>();

        // get the test
        if(id >= 0) {
            car = this.getEntityById(Car.class, id);

            // get the drivers
            car.current.currentDriver = new DriverService().getDriverById(car.current.currentDriverId);
            for(Car carHistory: car.history) {
                carHistory.currentDriver = new DriverService().getDriverById(carHistory.currentDriverId);
            }
        }

        return car;
    }

    /**
     * Returns driver by name without history
     * @param name of car to retrieve
     * @return Car without history
     */
    public Car getCarsByName(String name) {
        FdfEntity<Car> carWithHistory = getCarByNameWithHistory(name);
        if(carWithHistory != null && carWithHistory.current != null) {
            return carWithHistory.current;
        }
        return null;
    }

    /**
     * Custom query (name is not a member of CommonState)
     *
     * @param name of car to retrieve
     * @return FdfEnitty<Car> with history
     */
    public FdfEntity<Car> getCarByNameWithHistory(String name) {
        FdfEntity<Car> car = new FdfEntity<>();

        if(car != null) {
            // create the where statement for the query
            List<WhereClause> whereStatement = new ArrayList<>();

            // check that deleted records are not returned
            WhereClause whereDf = new WhereClause();
            whereDf.name = "df";
            whereDf.operator = WhereClause.Operators.IS_NOT;
            whereDf.value = "true";
            whereDf.valueDataType = Boolean.class;

            // add the id check
            WhereClause whereId = new WhereClause();
            whereId.conditional = WhereClause.CONDITIONALS.AND;
            whereId.name = "name";
            whereId.operator = WhereClause.Operators.EQUAL;
            whereId.value = name;
            whereId.valueDataType = String.class;

            whereStatement.add(whereDf);
            whereStatement.add(whereId);

            // do the query
            List<Car> returnedStates =
                    FdfPersistence.getInstance().selectQuery(Car.class, null, whereStatement);

            // create a List of entities
            return manageReturnedEntity(returnedStates);
        }

        return car;
    }
}
