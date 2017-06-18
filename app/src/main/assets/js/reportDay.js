/**
*   生成日报页面
*/
var vueApp = new Vue({
    el:'#app',
    data:{
        days:[],
        items:[]
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