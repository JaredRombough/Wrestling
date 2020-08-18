package openwrestling.model.segmentEnum;

import openwrestling.model.interfaces.iTransaction;

public enum TransactionType implements iTransaction {
    WORKER_MONTHLY {
        @Override
        public boolean isExpense() {
            return true;
        }
    },
    WORKER_APPEARANCE {
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
