/**
*   生成日报页面
*/

var vueApp = new Vue({
    el:'#app',
    data:{
        selectPayType: '',
        payTypeOptions: []
    }
})
var payTypeSelect2

$(document).ready(() => {
        console.log('page ready')
        payTypeSelect2 = $(".s2-pay-type").select2()
    });

/** Interface Function with java **/
function setupPayTypeOptions(payTypeOptions)   {
    vueApp.payTypeOptions = payTypeOptions
}