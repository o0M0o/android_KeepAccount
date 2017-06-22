/**
*   生成日报页面
*/


var vueApp = new Vue({
    el:'#app',
    data:{
        days:[],
        items:[]
    },
    updated : function()    {
        var tr = document.getElementsByClassName("trClass")
        for (var i = 0, len = tr.length; i < len; i++) {
            tr[i].style.backgroundColor = tr[i].classList.contains("pay_td") ? "#f9f9f9" : "#ffffff"
        }

        var td = document.getElementsByClassName("day_td")
        for (var i = 0, len = td.length; i < len; i++) {
            td[i].style.backgroundColor = 0 !== i % 2 ? "#ffffff" : "#ffffff"
        }
    }
})

function onLoadData(showData)   {
    vueApp.days = []
    for (var day in showData) {
        vueApp.days.push(day)

        var dayData = showData[day]
        for(var idx in dayData)  {
            var data = dayData[idx]
            data.noteType = data.payNote ? "支出" : "收入"
        }
    }

    vueApp.items = showData
}

$(document).ready(function () {
            console.log('page ready')
            //FixTable("tb_notes", 2, 600, 400);
        });