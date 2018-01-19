


package xxxx;



public enum CSValue {
    YES() {
        @Override
        public CompletionStatus getCompletionStatus() { 
            return CompletionStatus.COMPLETED_YES ;
        }
    },

    NO {
        @Override
        public CompletionStatus getCompletionStatus() { 
            return CompletionStatus.COMPLETED_NO ;
        }
    },

    MAYBE {
        @Override
        public CompletionStatus getCompletionStatus() { 
            return CompletionStatus.COMPLETED_MAYBE ;
        }
    } ;

    public abstract CompletionStatus getCompletionStatus() ;
}

