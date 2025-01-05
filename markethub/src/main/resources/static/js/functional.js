var globalStore = {};

// button clicks
$(document).on(
    'click', 
    '#quickpingbtn', 
    function () {
        $.get(
            '/ping', 
            function(response) {
                console.log(response);
                showRawData('#messages', response);
            }
        );
    }
);

//send-button
$(document).on(
    'click', 
    '#send-button', 
    function () {
        var request = JSON.stringify(formAsJson('#data-form'));
        $.ajax({
            url: '/send-msg',
            type: 'POST',
            data: request,
            contentType: 'application/json',
            success: function(response) {
                console.log(response);
                // var rawdata = JSON.parse(response);
                var data = response.data;
            }
        });
    }
);

// conditional formatting based on business data
function conditionalFormatting(type,key,value){
    
    if (type == 'person' && key == 'vote'){

        if (value == 'yes'){
            return 'positive';
        }
        else {
            return 'negative';
        }
    }
    else {
        return null;
    }
}

//adding actions to table
function conditionalActions(type, key, value){
    if (type == 'item' && key == 'id'){
        return '<button class="action" name="' + value + '">Edit Order</button>';
    }
}

$(document).on(
    'click', 
    ".action", 
    function () {
        var itemId = this.name;
        var obj = globalStore.inventory.find(item => item.id == itemId);
        console.log('action clicked ' + obj.itemName);
        $("input[name='itemName']").val(obj.itemName);
        $("input[name='unitPrice']").val(obj.unitMarketPrice);
    }
);

$(document).on(
    'click', 
    "#place-order", 
    function () {
        var request = JSON.stringify(formAsJson('#data-form'));
        $.ajax({
            url: '/placeOrder',
            type: 'POST',
            data: request,
            contentType: 'application/json',
            success: function(response) {
                console.log("response from placeOrder");
                console.log(response);
                var data = response.data;
            }
        });  
    }
);

// processing socket data
function functionelSockDataProcessing(rawData){
    if (rawData.metadata.type == 'person'){
        upsertToTable('#state-table', rawData.data, rawData.metadata.type);
        appendToTable('#log-table', rawData.data, rawData.metadata.type);
        showRawData('#messages', rawData);
    }
}

// on page load, load inventory data
$(document).ready(
    //if there is inventory-table on the page
    function(){
        if ($('#inventory-table').length){
            $.get(
                '/items',
                function(response){
                    globalStore.inventory = response;
                    console.log(globalStore.inventory);
                    upsertToTable('#inventory-table', response, 'item');
                }
            );
        }
    }
);

$(document).on(
    'click', 
    '#update-inventory', 
    function () {
        $.get(
            '/items',
            function(response){
                upsertToTable('#inventory-table', response, 'item');
            }
        );
    }
);


