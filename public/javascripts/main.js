(function() {
    var counter = 0;
    var Tester = {
        teams100: function() {
            var n = 100;
            counter += n;
            for (var i = 0; i < n; i++) {
                $.ajax('/teams100', {
                    method: 'post',
                    dataType: 'json',
                    success: function(result) {
                        console.log(result, "success");
                        counter--;
                        if (counter == 0) {
                            location.reload();
                        }
                        $('#counter').text(counter);
                    }
                });
                $('#counter').text(counter);
            }
        }
    };

    $(function() {
        $('#teams100Form').submit(function() {
            Tester.teams100();
        });
    });
})();
