const main = {
    init : function() {
        main.selectPopWord();
        setInterval(main.selectPopWord, 5000);
    },

    /** 실시간 검색어 조회 */
    selectPopWord : function () {
        $.ajax({
            type: 'GET',
            url: '/posts/rank',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8'
        }).done(function (data) {
    		data.forEach(function(val, idx) {
        		var tablebodyTag = "<tr><td>" + "<a href='/posts/search?page=1&searchKey=Y&keyword=" + val.keyword + "' target='_self'>" + val.keyword + " (" + val.view + ")" + "</a></td></tr>"; 
				$("#td"+(idx+1)).html(tablebodyTag); 
    		});
        }).fail(function (error) {
        });
    },
};

main.init();