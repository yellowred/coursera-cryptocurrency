// Block Chain should maintain only limited block nodes to satisfy the functions
// You should not have all the blocks added to the block chain in memory 
// as it would cause a memory overflow.

import java.util.ArrayList;

public class BlockChain {
    public static final int CUT_OFF_AGE = 10;

    private Block genesis;

    private ArrayList<Block> blockchain;
    private ArrayList<Transaction> txs;


    /**
     * create an empty block chain with just a genesis block. Assume {@code genesisBlock} is a valid
     * block
     */
    public BlockChain(Block genesisBlock) {
        this.genesis = genesisBlock;
        this.blockchain = new ArrayList<>();
        this.txs = new ArrayList<>();
    }

    /** Get the maximum height block */
    public Block getMaxHeightBlock() {
        if (blockchain.size() < 1) return this.genesis;
        return blockchain.get(blockchain.size() - 1);
    }

    /** Get the UTXOPool for mining a new block on top of max height block */
    public UTXOPool getMaxHeightUTXOPool() {
        UTXOPool utxoPool = new UTXOPool();
        getMaxHeightBlock().getTransactions().forEach(tx -> {
            for (int index = 0; index < tx.getOutputs().size(); index++) {
                UTXO utxo = new UTXO(tx.getHash(), index);
                utxoPool.addUTXO(utxo, tx.getOutput(index));
            }
        });
        return utxoPool;
    }

    /** Get the transaction pool to mine a new block */
    public TransactionPool getTransactionPool() {
        TransactionPool txpool = new TransactionPool();
        txs.forEach(txpool::addTransaction);
        return txpool;
    }

    /**
     * Add {@code block} to the block chain if it is valid. For validity, all transactions should be
     * valid and block should be at {@code height > (maxHeight - CUT_OFF_AGE)}.
     * 
     * <p>
     * For example, you can try creating a new block over the genesis block (block height 2) if the
     * block chain height is {@code <=
     * CUT_OFF_AGE + 1}. As soon as {@code height > CUT_OFF_AGE + 1}, you cannot create a new block
     * at height 2.
     * 
     * @return true if block is successfully added
     */
    public boolean addBlock(Block block) {

        if (block.getPrevBlockHash() == null) return false;

        //  check for validity
        /*
        block.getTransactions().stream().allMatch(tx -> {
            getMaxHeightUTXOPool().
        })
        */

        blockchain.add(block);
        System.out.println(String.format("Block added : %s", new String(block.getHash())));
        return true;
    }

    /** Add a transaction to the transaction pool */
    public void addTransaction(Transaction tx) {
        txs.add(tx);
    }
}