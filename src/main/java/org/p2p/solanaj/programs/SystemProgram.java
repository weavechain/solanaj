package org.p2p.solanaj.programs;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.TransactionInstruction;
import org.p2p.solanaj.core.AccountMeta;
import org.p2p.solanaj.utils.ShortvecEncoding;

import static org.bitcoinj.core.Utils.*;

public class SystemProgram extends Program {
    public static final PublicKey PROGRAM_ID = new PublicKey("11111111111111111111111111111111");

    public static final int PROGRAM_INDEX_CREATE_ACCOUNT = 0;
    public static final int PROGRAM_INDEX_ASSIGN = 1;
    public static final int PROGRAM_INDEX_TRANSFER = 2;
    public static final int PROGRAM_INDEX_CREATE_WITH_SEED = 3;


    public static TransactionInstruction transfer(PublicKey fromPublicKey, PublicKey toPublickKey, long lamports) {
        ArrayList<AccountMeta> keys = new ArrayList<AccountMeta>();
        keys.add(new AccountMeta(fromPublicKey, true, true));
        keys.add(new AccountMeta(toPublickKey, false, true));

        // 4 byte instruction index + 8 bytes lamports
        byte[] data = new byte[4 + 8];
        ShortvecEncoding.uint32ToByteArrayLE(PROGRAM_INDEX_TRANSFER, data, 0);
        ShortvecEncoding.int64ToByteArrayLE(lamports, data, 4);

        return createTransactionInstruction(PROGRAM_ID, keys, data);
    }

    public static TransactionInstruction createAccount(PublicKey fromPublicKey, PublicKey newAccountPublickey,
            long lamports, long space, PublicKey programId) {
        ArrayList<AccountMeta> keys = new ArrayList<AccountMeta>();
        keys.add(new AccountMeta(fromPublicKey, true, true));
        keys.add(new AccountMeta(newAccountPublickey, false, true));

        byte[] data = new byte[4 + 8 + 8 + 32];
        ShortvecEncoding.uint32ToByteArrayLE(PROGRAM_INDEX_CREATE_ACCOUNT, data, 0);
        ShortvecEncoding.int64ToByteArrayLE(lamports, data, 4);
        ShortvecEncoding.int64ToByteArrayLE(space, data, 12);
        System.arraycopy(programId.toByteArray(), 0, data, 20, 32);

        return createTransactionInstruction(PROGRAM_ID, keys, data);
    }

    public static TransactionInstruction createAccountWithSeed(PublicKey fromPublicKey, PublicKey basePublicKey, PublicKey newAccountPublickey, String seed, long lamports, long space, PublicKey programId) {
        ArrayList<AccountMeta> keys = new ArrayList<AccountMeta>();
        keys.add(new AccountMeta(fromPublicKey, true, true));
        keys.add(new AccountMeta(newAccountPublickey, false, true));
        if (!Objects.equals(basePublicKey, fromPublicKey)) {
            keys.add(new AccountMeta(basePublicKey, true, false));
        }

        byte[] data = new byte[1024];
        int idx = 0;
        ShortvecEncoding.uint32ToByteArrayLE(PROGRAM_INDEX_CREATE_WITH_SEED, data, idx);
        idx += 4;
        System.arraycopy(basePublicKey.toByteArray(), 0, data, idx, 32);
        idx += 32;

        ShortvecEncoding.uint32ToByteArrayLE(seed.length(), data, idx);
        idx += 4;
        int padding = 0;
        ShortvecEncoding.uint32ToByteArrayLE(padding, data, idx);
        idx += 4;
        System.arraycopy(seed.getBytes(StandardCharsets.UTF_8), 0, data, idx, seed.length());
        idx += seed.length();

        ShortvecEncoding.int64ToByteArrayLE(lamports, data, idx);
        idx += 8;
        ShortvecEncoding.int64ToByteArrayLE(space, data, idx);
        idx += 8;
        System.arraycopy(programId.toByteArray(), 0, data, idx, 32);
        idx += 32;

        byte[] instruction = new byte[idx];
        System.arraycopy(data, 0, instruction, 0, instruction.length);

        return createTransactionInstruction(PROGRAM_ID, keys, instruction);
    }
}
