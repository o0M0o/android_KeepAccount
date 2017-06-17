/**
*   工具集合
*/
function printObj(myObj){
    var output = " "
    for (var myKey in myObj) {
        if (myObj.hasOwnProperty(myKey)) {
            var propVal = myObj[myKey];
            output += myKey + '(' + typeof(myKey) + ')' + ':';
            if (typeof(myObj[myKey]) == 'string') {
                propVal += myObj[myKey];
            } else {
                if (propVal != null && propVal.toString) {
                    output += propVal.toString();
                } else {}
            }

            output += '; '
        }
    }

    console.log('[' + output + ']')
}
