angular
    .module('signIn')
    .controller('signInController', function ($scope, $http, $location, $rootScope, $route, mainService, $window, signInService) {
        $scope.message = 'Войти.';
        $scope.params = $location.search();
        $scope.mode = $scope.params.mode;

        $scope.vm = {};
        $scope.vm.login = $scope.login;

        $window.localStorage.setItem('token', '');

        $scope.login = function () {
            $http({
                url: '/login',
                method: "POST",
                data: {
                    'userName': $scope.vm.username,
                    'password': $scope.vm.password
                }
            }).then(function (response) {
                console.log("response=" + JSON.stringify(response));
                if (response.data) {
                    var token
                        = $window.btoa($scope.vm.username + ':' + $scope.vm.password);
                    var userData = {
                        userName: $scope.vm.username,
                        authData: token
                    }
                    $window.sessionStorage.setItem(
                        'userData', JSON.stringify(userData)
                    );
                    $http.defaults.headers.common['Authorization']
                        = 'Basic ' + token;
                    console.log("Autorized");
                    console.log("userData = " + JSON.stringify(userData));
                    $location.path('/main');
                } else {
                    alert("Authentication failed.")
                }
            }, function errorCallback(response) {
                alert("Authentication failed.");
                console.log("error response=" + JSON.stringify(response));
            });
        };

        /*$scope.login2 = function () {
            signInService.login($scope.user,
                function (result) {
                    console.log("authres = " + JSON.stringify(result));
                    if (result) {
                        var token
                            = $window.btoa($scope.vm.username + ':' + $scope.vm.password);
                        var userData = {
                            userName: $scope.vm.username,
                            authData: token
                        }
                        $window.sessionStorage.setItem(
                            'userData', JSON.stringify(userData)
                        );
                        $http.defaults.headers.common['Authorization']
                            = 'Basic ' + token;
                        console.log("Autorized2");
                        console.log("userData2 = " + JSON.stringify(userData));
                        $location.path('/main');

                    } else {
                        alert("Authentication failed.")
                    }
                },
                function (error) {
                    $rootScope.currentRole = {role: 'ROLE_ANONYMOUS'};
                    console.error(error);
                });
        }*/
    });

