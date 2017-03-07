
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class TxHandler {

    private UTXOPool ledger;

    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        ledger = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool,
     * (2) the signatures on each input of {@code tx} are valid,
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        // IMPLEMENT THIS

        //  (1)
        if (
                !tx.getInputs().stream()
                        .map(input -> new UTXO(input.prevTxHash, input.outputIndex))
                        .allMatch(utxo -> ledger.contains(utxo))
                ) return false;

        System.out.println("Pass 1");

        //  (2)
        for (int index = 0; index < tx.getInputs().size(); index++) {
            Transaction.Input input = tx.getInput(index);
            UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
            if (!Crypto.verifySignature(ledger.getTxOutput(utxo).address, tx.getRawDataToSign(index), input.signature)) {
                return false;
            }
        }

        System.out.println("Pass 2");

        //  (3)
        if (
                tx.getInputs().stream()
                        .map(input -> new UTXO(input.prevTxHash, input.outputIndex))
                        .map(utxo -> utxo.hashCode())
                        .collect(Collectors.toSet())
                        .size() != tx.getInputs().size()
                ) return false;

        System.out.println("Pass 3");

        //  (4)
        if (!tx.getOutputs().stream().allMatch(output -> output.value >= 0)) return false;

        System.out.println("Pass 4");

        //  (5)
        Double sum1 = tx.getInputs().stream()
                .mapToDouble(input -> ledger.getTxOutput(new UTXO(input.prevTxHash, input.outputIndex)).value)
                .sum();
        Double sum2 = tx.getOutputs().stream().mapToDouble(output -> output.value).sum();
        System.out.println(String.format("Sums %s : %s : %s", sum1, sum2, sum1 >= sum2));
        if (
                tx.getInputs().stream()
                        .mapToDouble(input -> ledger.getTxOutput(new UTXO(input.prevTxHash, input.outputIndex)).value)
                        .sum() < tx.getOutputs().stream().mapToDouble(output -> output.value).sum()
                ) return false;

        System.out.println("Pass 5");

        return true;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        return Arrays.stream(possibleTxs)
                .filter(trn -> isValidTx(trn))
                .peek(trn -> {
                    for (Transaction.Input input : trn.getInputs()) {
                        ledger.removeUTXO(new UTXO(input.prevTxHash, input.outputIndex));
                    }
                    for (int index = 0; index < trn.getOutputs().size(); index++) {
                        ledger.addUTXO(new UTXO(trn.getHash(), index), trn.getOutput(index));
                    }
                })
                .toArray(Transaction[]::new);
    }

}
