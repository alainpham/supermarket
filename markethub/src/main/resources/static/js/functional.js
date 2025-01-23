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

//conditional header
function conditionalHeaders(type){
    if (type == 'item'){
        return '<th>actions</th>';
    }
    else if (type == 'order'){
        return '<th>actions</th>';
    }
    else {
        return null
    }
}

//adding actions to table
function conditionalActions(type, key, value){
    if (type == 'item' && key == 'id'){
        return '<td><button class="buy" name="' + value + '">Buy</button><button class="sell" name="' + value + '">Sell</button></td>';
    }
    if (type == 'order' && key == 'id'){
        return '<td><button class="cancel-order" name="' + value + '">Cancel</button></td>';
    }
}

$(document).on(
    'click', 
    ".buy", 
    function () {
        var itemId = this.name;
        var obj = globalStore.inventory.find(item => item.id == itemId);
        console.log('action clicked ' + obj.itemName);
        $("input[name='itemName']").val(obj.itemName);
        $("input[name='unitPrice']").val(obj.unitMarketPrice);
        $("select[name='type']").val("buy");
    }
);

$(document).on(
    'click', 
    ".sell", 
    function () {
        var itemId = this.name;
        var obj = globalStore.inventory.find(item => item.id == itemId);
        console.log('action clicked ' + obj.itemName);
        $("input[name='itemName']").val(obj.itemName);
        $("input[name='unitPrice']").val(obj.unitMarketPrice);
        $("select[name='type']").val("sell");
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
                globalStore.orders.push(response);
                upsertToTable('#orders-table', [response], 'order');

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
                    if (response.length > 0){
                        globalStore.inventory = response;
                        console.log(globalStore.inventory);
                        upsertToTable('#inventory-table', response, 'item');
                    }else
                    {
                        globalStore.inventory = [];
                    }
                }
            );
        }

        if ($('#orders-table').length){
            $.get(
                '/orders',
                function(response){
                    console.log(response);
                    if (response.length > 0){
                        globalStore.orders = response;
                        upsertToTable('#orders-table', response, 'order');
                    }else {
                        globalStore.orders = [];
                    }
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
                if (response.length > 0){
                    globalStore.inventory = response;
                    upsertToTable('#inventory-table', response, 'item');
                }
            }
        );
    }
);

$(document).on(
    'click', 
    '#update-orders', 
    function () {
        $.get(
            '/orders',
            function(response){
                if (response.length > 0){
                    globalStore.orders = response;
                    upsertToTable('#orders-table', response, 'order');
                }
            }
        );
    }
);

