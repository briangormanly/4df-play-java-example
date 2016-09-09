// Use internal $.serializeArray to get list of form elements which is consistent with $.serialize
//
// And to avoid names such as
// => object["favorite-color"]
//
// We camelcase the name part, so the notation becomes
// => object["favoriteColor"]
//
// Conveniently, this allows period notation to be used.
// => object.favoriteColor
//
// This behaviour is similar to $(element).data()
//
// $('<div data-favorite-color="yellow"></div>').data()
// => { favoriteColor: 'yellow' }
$.fn.serializeObject = function() {
    var o = Object.create(null),
        elementMapper = function(element) {
            element.name = $.camelCase(element.name);
            return element;
        },
        appendToResult = function(i, element) {
            var node = o[element.name];

            if ('undefined' != typeof node && node !== null) {
                o[element.name] = node.push ? node.push(element.value) : [node, element.value];
            } else {
                o[element.name] = element.value;
            }
        };

    $.each($.map(this.serializeArray(), elementMapper), appendToResult);
    return o;
};

/* Car */
function editCar(car) {
    if(car != undefined) {
        $("#year").val(car.current.year);
        $("#make").val(car.current.make);
        $("#model").val(car.current.model);
        $("#color").val(car.current.color);
        $("#name").val(car.current.name);
        $("#description").val(car.current.description);
        $("#broken").prop('checked', false);
        $("#id").val(car.current.id);

        if(car.current.isInNeedOfRepair) {
            $("#isInNeedOfRepair").prop('checked', true);
        }
        $("#isOnCall").prop('checked', false);
        if(car.current.isOnCall) {
            $("#isOnCall").prop('checked', true);
        }
        $("#isOutWorking").prop('checked', false);
        if(car.current.isOutWorking) {
            $("#isOutWorking").prop('checked', true);
        }

        getAllDrivers(car.current.currentDriverId);
    }

    $("#edit-car").modal("toggle");

}


function editDriver(driver) {
    if(driver != undefined) {
        $("#fristName").val(driver.current.firstName);
        $("#lastName").val(driver.current.lastName);
        $("#phoneNumber").val(driver.current.phoneNumber);
    }

    $("#edit-driver").modal("toggle");

}

function getAllDrivers(currentDriverId) {
    $.get('/allDrivers', {}, function(res,resp, jqXHR) {
        var driverSelect = "<select name='currentDriverId'>";
        driverSelect += "<option value='-1'>No Driver</option>";
        $.each(res, function(i, driver) {
            if(driver.entityId == currentDriverId) {
                driverSelect += "<option selected>" + driver.current.firstName + " "
                    + driver.current.lastName + "</option>";
            }
            else {
                driverSelect += "<option value='" + driver.current.id + "'>" + driver.current.firstName + " " + driver.current.lastName + "</option>";
            }
        });
        driverSelect += "</select>";

        $("#driver-records").html(driverSelect);
    }, "json");
}

function getAllCars() {
    $.get('/allCars', {}, function(res,resp, jqXHR) {
        var cars = "";
        $.each(res, function(i, car) {
            cars += "<p>" + car.current.year + " " + car.current.make + " "
                + car.current.model + " (" + car.current.name + ")</p>";
            cars += "<ul><li>" + car.current.description + "</li>";
            cars += "<li>Broken? " + car.current.isInNeedOfRepair + "</li>";
            cars += "<li>Occupied? " + car.current.isOnCall + "</li>";
            cars += "<li>Working? " + car.current.isOutWorking + "</li>";
            if(car.current.currentDriver != null) {
                cars += "<li>Driver " + car.current.currentDriver.firstName + " "
                    + car.current.currentDriver.lastName + "</li>";
            } else {
                cars += "<li>No Driver</li>";
            }
            cars += "</ul>";
            if(car.history.length > 0) {
                $.each(car.history, function(j, historicalCar) {
                    cars += "<div class='car-history'>historical data:<br />";
                    cars += "Dates Active: " + historicalCar.arsd + " to: " + historicalCar.ared + "<br />";
                    cars += "<p>" + historicalCar.year + " " + historicalCar.make + " "
                        + historicalCar.model + " (" + historicalCar.name + ")</p>";
                    cars += "<ul><li>" + historicalCar.description + "</li>";
                    cars += "<li>Broken? " + historicalCar.isInNeedOfRepair + "</li>";
                    cars += "<li>Occupied? " + historicalCar.isOnCall + "</li>";
                    cars += "<li>Working? " + historicalCar.isOutWorking + "</li>";
                    if(historicalCar.currentDriver != null) {
                        cars += "<li>Driver " + historicalCar.currentDriver.firstName + " "
                            + historicalCar.currentDriver.lastName + "</li>";
                    } else {
                        cars += "<li>No Driver</li>";
                    }
                    cars += "</ul>";
                });
            }
            cars += "<button type='button' class='btn btn-default btn-lg' id='editCarButton' onclick='editCar(" + JSON.stringify(car) + ");$(\"#edit-car\").modal()'>Edit</button>";
            cars += "<hr />";
        });

        $("#car-records").html(cars);
    }, "json");
}

function getAllDrivers() {
    $.get('/allDrivers', {}, function(res,resp, jqXHR) {
        var drivers = "";
        $.each(res, function(i, driver) {
            drivers += "<p>" + driver.current.firstName + " " + driver.current.lastName + " phone: " + driver.current.phoneNumber + "</p><hr />";
        });

        $("#driver-records").html(drivers);
    }, "json");
}


$(document).ready(function() {

    $("#car-submit").click(function () {
        $.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            type: "POST",
            url: "/saveCar",
            data: JSON.stringify(jQuery('#carForm').serializeObject()),
            dataType: 'json',
            success:function(data) {
                $("#edit-car").modal("toggle");
                getAllCars();
            }
        });
    });

    $("#driver-submit").click(function () {
        $.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            type: "POST",
            url: "/saveDriver",
            data: JSON.stringify(jQuery('#driverForm').serializeObject()),
            dataType: 'json',
            success:function(data) {
                $("#edit-driver").modal("toggle");
                getAllDrivers();
            }
        });
    });
});
