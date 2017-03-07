import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.*;

public class Main {

    public static void main(String[] args)   throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
	    System.out.println("Start mining...");


        Security.addProvider(new BouncyCastleProvider());
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        keyGen.initialize(1024, random);

        KeyPair pair = keyGen.generateKeyPair();
        Block genBlock = new Block(null, pair.getPublic());
        genBlock.finalize();
        BlockChain bc = new BlockChain(genBlock);
        BlockHandler blockHandler = new BlockHandler(bc);

        pair = keyGen.generateKeyPair();
        Transaction tx = new Transaction();
        tx.addInput(genBlock.getHash(), 0);
        tx.addOutput(25, pair.getPublic());
        blockHandler.processTx(tx);

        pair = keyGen.generateKeyPair();
        Block blockIn = blockHandler.createBlock(pair.getPublic());


        Block blockOut = bc.getMaxHeightBlock();

        System.out.println(String.format("Block In : %s", new String(blockIn.getHash())));
        System.out.println(String.format("Block Out : %s", new String(blockOut.getHash())));
    }
}
