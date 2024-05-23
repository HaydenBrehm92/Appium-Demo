package core.constants;

public interface GrepConstants {
    String recentCalls = "POCB_MBCP_CALL_CONNECT|" +
            "POCB_MBCP_CONNECT_ACK|" +
            "POCB_MBCP_CALL_DISCONNECT|" +
            "POCB_MBCP_FLOOR_FREE|" +
            "POCB_MBCP_FLOOR_TAKEN|" +
            "POCB_MBCP_FLOOR_GRANTED|" +
            "POCB_MBCP_WAKE_UP_NOTIFY_SENT|" +
            "POCB_MBCP_WAKE_UP_NOTIFY_RECEIVED|" +
            "DecodeTBCPConnectSDES|" +
            "Sending SIP msg|" +
            "Received SIP msg|" +
            "SIP/2.0";

    String ptxMessages = "PluginMcdataMessagingCb.msgReceiptRecvdCb()|" +
            "PtxUtils.sendPtxAttachment()|" +
            "MessagingHelper.getAttachmentInfo()";

    String kpiLogs = "KOD PERF";

    String grepLines = kpiLogs + "|" +
            ptxMessages + "|" + "POCB_MBCP_FLOOR_GRANTED"; // last part for 11.x compatability
    


}
