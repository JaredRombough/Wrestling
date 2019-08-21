package wrestling.model.segmentEnum;

import wrestling.model.interfaces.iTransaction;

public enum TransactionType implements iTransaction {
    WORKER {
        @Override
        public boolean isExpense() {
            return true;
        }
    },
    STAFF {
        @Override
        public boolean isExpense() {
            return true;
        }
    },
    GATE {
        @Override
        public boolean isExpense() {
            return false;
        }
    }
}
