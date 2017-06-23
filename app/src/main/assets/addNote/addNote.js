/**
*   生成日报页面
*/

var vueApp = new Vue({
    el:'#app',
    data:{
        selectPayType: '',
        payTypeOptions: []
    },
    updated : function()    {
        console.log('page update')
    }
})

$(document).ready(() => {
        $(".s2-pay-type").select2({
            allowClear: true,
            theme: "classic"
        })
        console.log('page ready')
    });

/** Interface Function with java **/
function setupPayTypeOptions(payTypeOptions)   {
    vueApp.payTypeOptions = []
    for(var idx in payTypeOptions)  {
        var opt = new Object()
        opt.text = payTypeOptions[idx].type
        opt.value = payTypeOptions[idx].id

        vueApp.payTypeOptions.push(opt)
    }
}