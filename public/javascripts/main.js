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