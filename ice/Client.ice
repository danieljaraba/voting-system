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

    interface ClientCallback
    {
        void sendIndividualResponse(IndividualResponse r);
        void sendMultipleResponse(MultipleResponse r);
    }

    interface ClientResolver
    {
        void sendId(string id, ClientCallback* client);
        void sendFile(ResponseList list, ClientCallback* client, bool isLast);
        void setThreadNumber(int threadCount, ClientCallback* client);
    }
}