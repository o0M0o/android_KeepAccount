/**
*   生成日报页面
*/
var vueApp = new Vue({
    el:'#app',
    data:{
        message:'abc'
    }
})

function onLoadData(showData)   {
    printObj(showData)
    var d = new Date()
    vueApp.message = 'load data at ' + d.getTime()
    return vueApp.message
}