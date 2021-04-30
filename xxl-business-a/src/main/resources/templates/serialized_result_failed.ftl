<div style='color:#323232;'>
    <p style='margin-left:50px;'>
        Unfortunately, the serialized data processing is failed at stage ${errorTask}.<br/>
        Please contact related personnel from xxl for further investigation on this failure.
    </p>

    <div style='width:800px;margin-left:25px;'>
        <table cellpadding='0' cellspacing='0' width='100%'
               style='border-collapse:collapse;color:#666; empty-cells:show'>
            <tr style='line-height:25px;text-align:left;'>
                <th style='border:1px solid #ccc;'>Name</th>
                <td style='border:1px solid #ccc;'>${name}</td>
            </tr>
            <tr style='line-height:25px;text-align:left;'>
                <th style='border:1px solid #ccc;'>Access URL</th>
                <td style='border:1px solid #ccc;'><a href='${accessUrl}'>${accessUrl}</a></td>
            </tr>
            <tr style='line-height:25px;text-align:left;'>
                <th style='border:1px solid #ccc;'>Snapshot URL</th>
                <td style='border:1px solid #ccc;'><a href='${historyUrl}'>${historyUrl}</a></td>
            </tr>
            <tr style='line-height:25px;text-align:left;'>
                <th style='border:1px solid #ccc;'>Processing Time</th>
                <td style='border:1px solid #ccc;'>${useTime}</td>
            </tr>
            <tr style='line-height:25px;text-align:left;'>
                <th style='border:1px solid #ccc;'>Warning Count</th>
                <td style='border:1px solid #ccc;'>${warnSize}</td>
            </tr>
            <tr style='line-height:25px;text-align:left;'>
                <th style='border:1px solid #ccc;'>Error Step</th>
                <td style='border:1px solid #ccc;'>${errorTask}</td>
            </tr>

        </table>
    </div>
    <p style='font-weight: bold;margin-left:25px;'>This is an automatically generated message by serialized process, please do not reply to this message！</p>
</div>
