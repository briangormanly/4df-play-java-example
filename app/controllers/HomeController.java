package controllers;

import com.fdflib.model.entity.FdfEntity;
import com.fdflib.service.GenericService;
import models.Car;
import models.Driver;
import play.*;
import play.libs.Json;
import play.mvc.*;

import services.CarService;
import services.DriverService;
import views.html.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    CarService cs = new CarService();
    DriverService ds = new DriverService();

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {

        return ok(index.render("Welcome to the 4dfLib Play Example!"));
    }

    public Result getAllCars() {
        // get all cars using the getAllCars method in CarService that overrides the getAll in FdfCommonServices with
        // custom logic
        List<FdfEntity<Car>> allCars = cs.getAllCars();

        return ok(Json.toJson(allCars));
    }

    public Result getAllDrivers() {
        // get the data - note that we did not create getAll() methods in the Driver and Car Services, here we are
        // using the FdfLib APIs directly.
        List<FdfEntity<Driver>> allDrivers = ds.getAll(Driver.class);

        return ok(Json.toJson(allDrivers));
    }

}
