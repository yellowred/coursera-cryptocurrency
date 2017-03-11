import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.*;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;


public class Main {

  public static void main(String[] args)   throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    System.out.println("Start mining...");

    test1();

    /*
    // Как из генезис блока получтить UTXO

    pair = keyGen.generateKeyPair();
    Transaction tx = new Transaction();
    tx.addInput(coinbaseTx.getHash(), 0);
    tx.addOutput(25, pair.getPublic());

    Signature signature2 = Signature.getInstance("SHA256withRSA");
    signature2.initSign(pair.getPrivate());
    signature2.update(tx.getRawDataToSign(0));
    tx.addSignature(signature2.sign(), 0);

    tx.finalize();
    blockHandler.processTx(tx);

    pair = keyGen.generateKeyPair();
    Block blockIn = blockHandler.createBlock(pair.getPublic());


    Block blockOut = bc.getMaxHeightBlock();

    System.out.println(String.format("BlockChain TXs count : %s", bc.getTransactionPool().getTransactions().size()));
    System.out.println(String.format("BlockChain UTXOs count : %s", bc.getMaxHeightUTXOPool().getAllUTXO().size()));
    System.out.println(String.format("Block In : %s", (new HexBinaryAdapter()).marshal(blockIn.getHash())));
    System.out.println(String.format("Block In TXs count: %s", blockIn.getTransactions().size()));
    System.out.println(String.format("Block Out : %s", (new HexBinaryAdapter()).marshal(blockOut.getHash())));
    System.out.println(String.format("Block Out TXs count: %s", blockOut.getTransactions().size()));
    */
  }

  public static void test1()    throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    System.out.println("Process a block with a single valid transaction");

    Security.addProvider(new BouncyCastleProvider());
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
    SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
    keyGen.initialize(1024, random);
    KeyPair pair = keyGen.generateKeyPair();


    Block genesisBlock = new Block(null, pair.getPublic());
    genesisBlock.finalize();

    BlockChain blockChain = new BlockChain(genesisBlock);
    BlockHandler blockHandler = new BlockHandler(blockChain);

    KeyPair pair1 = keyGen.generateKeyPair();
    Block block = new Block(genesisBlock.getHash(), pair1.getPublic());

    Transaction spendCoinbaseTx = new Transaction();
    spendCoinbaseTx.addInput(genesisBlock.getCoinbase().getHash(), 0);
    spendCoinbaseTx.addOutput(10, pair1.getPublic());

    Signature signature = Signature.getInstance("SHA256withRSA");
    signature.initSign(pair.getPrivate());
    signature.update(spendCoinbaseTx.getRawDataToSign(0));
    spendCoinbaseTx.addSignature(signature.sign(), 0);

    spendCoinbaseTx.finalize();
    block.addTransaction(spendCoinbaseTx);

    Transaction spendCoinbaseTx2 = new Transaction();
    spendCoinbaseTx2.addInput(genesisBlock.getCoinbase().getHash(), 0);
    spendCoinbaseTx2.addOutput(10, pair1.getPublic());

    Signature signature2 = Signature.getInstance("SHA256withRSA");
    signature2.initSign(pair.getPrivate());
    signature2.update(spendCoinbaseTx2.getRawDataToSign(0));
    spendCoinbaseTx2.addSignature(signature2.sign(), 0);

    spendCoinbaseTx2.finalize();
    block.addTransaction(spendCoinbaseTx2);

    block.finalize();

    Boolean passed =  blockHandler.processBlock(block);

    System.out.println(String.format("Passed : %s", passed));
    System.out.println(String.format("Txs : %s", block.getTransactions().size()));


    KeyPair pair2 = keyGen.generateKeyPair();
    Block block2 = new Block(genesisBlock.getHash(), pair2.getPublic());

    Transaction spendCoinbaseTx3 = new Transaction();
    spendCoinbaseTx3.addInput(genesisBlock.getCoinbase().getHash(), 0);
    spendCoinbaseTx3.addOutput(5, pair1.getPublic());

    Signature signature3 = Signature.getInstance("SHA256withRSA");
    signature3.initSign(pair.getPrivate());
    signature3.update(spendCoinbaseTx3.getRawDataToSign(0));
    spendCoinbaseTx3.addSignature(signature3.sign(), 0);

    spendCoinbaseTx3.finalize();
    block2.addTransaction(spendCoinbaseTx3);

    block2.finalize();

    Boolean passed2 =  blockHandler.processBlock(block2);


    System.out.println(String.format("Passed : %s", passed2));
    System.out.println(String.format("Txs : %s", block2.getTransactions().size()));
  }

  public static void test2()    throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    Security.addProvider(new BouncyCastleProvider());
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
    SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
    keyGen.initialize(1024, random);

    KeyPair pair = keyGen.generateKeyPair();
    Block genesisBlock = new Block(null, pair.getPublic());
    genesisBlock.finalize();

    BlockChain bc = new BlockChain(genesisBlock);
    BlockHandler blockHandler = new BlockHandler(bc);

    System.out.println(String.format("BlockChain UTXOs count : %s", bc.getMaxHeightUTXOPool().getAllUTXO().size()));

    boolean passes = true;
    Transaction spendCoinbaseTx;
    Block prevBlock = genesisBlock;
    Signature signature = Signature.getInstance("SHA256withRSA");
    pair = keyGen.generateKeyPair();

    for (int i = 0; i < 1; i++) {
      spendCoinbaseTx = new Transaction();
      spendCoinbaseTx.addInput(prevBlock.getCoinbase().getHash(), 0);
      spendCoinbaseTx.addOutput(Block.COINBASE, pair.getPublic());

      signature.initSign(pair.getPrivate());
      signature.update(spendCoinbaseTx.getRawDataToSign(0));

      spendCoinbaseTx.addSignature(signature.sign(), 0);
      spendCoinbaseTx.finalize();
      blockHandler.processTx(spendCoinbaseTx);

      Block createdBlock = blockHandler.createBlock(pair.getPublic());

      passes = passes && createdBlock != null && createdBlock.getPrevBlockHash().equals(prevBlock.getHash()) && createdBlock.getTransactions().size() == 1 && createdBlock.getTransaction(0).equals(spendCoinbaseTx);
      prevBlock = createdBlock;
    }

    System.out.println(String.format("Passes : %s", passes));
  }
}
