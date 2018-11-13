var kappersApp = angular.module('kappersApp',
    [
        'ngRoute'
        , 'ngResource'
        , 'ngAnimate'
        , 'main'
        , 'about'
        , 'contacts'
        , 'profile'
        , 'signIn'
        , 'signUp'
    ]
);

kappersApp.config(function ($routeProvider) {
    $routeProvider
        .when('/', {
            templateUrl: './ui/view/main/main.html',
            controller: 'mainController'
        })
        .otherwise({
            templateUrl: './ui/view/404.html'
        })
    ;
});

kappersApp.run(['$rootScope', '$location', function ($rootScope, $location) {
    $rootScope.routeTo = function (path) {
        switch (path) {
            case '/':
                $rootScope.currentNavigation = 'main';
                break;
            case '/about':
                $rootScope.currentNavigation = 'about';
                break;
            case '/contacts':
                $rootScope.currentNavigation = 'contacts';
                break;
            case '/profile':
                $rootScope.currentNavigation = 'profile';
                break;
            case '/sign-in':
                $rootScope.currentNavigation = 'sign-in';
                break;
            case '/sign-up':
                $rootScope.currentNavigation = 'sign-up';
                break;
            default :
                path = '404';
                $rootScope.currentNavigation = '404';
        }
        $location.path(path).search({});
    }

    $rootScope.currentPage = 'main';

    $rootScope.DEFAULT_STATES = [STATE.ACTIVE, STATE.DELETED];

    $rootScope.stateFilter = function (entity) {
        if (entity && entity.state == STATE.ACTIVE) {
            return true;
        }
        return false;
    }

    $rootScope.toInt = function (id) {
        return parseInt(id, 10);
    };

    $location.path("/").replace();
}]);