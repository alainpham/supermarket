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
                var rawdata = JSON.parse(response);
                var data = rawdata.data;
                var type = rawdata.metadata.type;
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

// processing socket data
function functionelSockDataProcessing(rawData){
    if (rawData.metadata.type == 'person'){
        upsertToTable('#state-table', rawData.data, rawData.metadata.type);
        appendToTable('#log-table', rawData.data, rawData.metadata.type);
        showRawData('#messages', rawData);
    }
}