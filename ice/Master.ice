module ServerIce
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

    interface MasterCallback
    {
        void sendIndividualResponse(IndividualResponse r);
        void sendMultipleResponse(MultipleResponse r);
    }

    interface MasterResolver
    {
        void sendId(string id, MasterCallback* master);
        void sendFile(ResponseList list, MasterCallback* master);
        void setThreadNumber(int threadCount, MasterCallback* master);
        void setChunkSize(int chunkSize, MasterCallback* master);
    }
}