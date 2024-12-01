module ClientIce
{
    sequence<string> ResponseList;

    class IndividualResponse{
        long responseTime;
        string value;
    }

    class MultipleResponse{
        long responseTime;
        ResponseList values;
    }

    interface Callback
    {
        void sendIndividualResponse(IndividualResponse r);
        void sendMultipleResponse(MultipleResponse r);
    }

    interface Printer
    {
        void sendId(string id, Callback* client);
        void sendFile(ResponseList list, Callback* client);
        void setThreadNumber(int threadCount, Callback* client);
    }
}