/**
 * @mainpage RC4 Алгоритм Шифрования
 *
 * @section intro Введение
 *
 * Этот проект реализует алгоритм шифрования RC4 на языке Java.
 * Алгоритм RC4 представляет собой потоковый шифр, который используется для шифрования и дешифрования данных.
 *
 * @section usage Использование
 *
 * Для шифрования или дешифрования текста, используйте методы encrypt и decrypt:
 *
 * @code
 * String key = "not-so-random-key";
 * String plaintext = "Chu pa pi";
 * String ciphertext = RC4.encrypt(key, plaintext);
 * System.out.println("ciphertext: " + ciphertext);
 * String decrypted = RC4.decrypt(key, ciphertext);
 * System.out.println("decrypted: " + decrypted);
 * @endcode
 *
 * @section methods Методы
 *
 * Основные методы включают:
 * - KSA: Алгоритм инициализации ключа.
 * - PRGA: Алгоритм генерации псевдослучайной последовательности.
 * - getKeystream: Генерация потока ключей.
 * - encryptLogic: Основная логика шифрования/дешифрования.
 * - encrypt: Шифрование текста.
 * - decrypt: Дешифрование текста.
 */


/**
 * @file RC4.java
 * @brief Реализует алгоритм шифрования RC4.
 */

/**
 * @class RC4
 * @brief Класс, предоставляющий методы для шифрования и дешифрования с использованием RC4.
 */
public class RC4 {
    private static final int MOD = 256;

    /**
     * @brief Алгоритм инициализации ключа (KSA)
     * @param key - Ключ шифрования в виде массива байт.
     * @return Инициализированный массив S.
     *
     * Этот метод инициализирует массив S на основе предоставленного ключа.
     */
    public static int[] KSA(byte[] key) {
        int keyLength = key.length;
        int[] S = new int[MOD];
        for (int i = 0; i < MOD; i++) {
            S[i] = i;
        }
        int j = 0;
        for (int i = 0; i < MOD; i++) {
            j = (j + S[i] + (key[i % keyLength] & 0xFF)) % MOD;
            int temp = S[i];
            S[i] = S[j];
            S[j] = temp;
        }
        return S;
    }

    /**
     * @brief Алгоритм генерации псевдослучайной последовательности (PRGA)
     * @param S - Инициализированный массив.
     * @return Iterable, генерирующий поток ключей.
     *
     * Этот метод генерирует поток ключей на основе массива S.
     */
    public static Iterable<Integer> PRGA(int[] S) {
        return () -> new java.util.Iterator<Integer>() {
            int i = 0;
            int j = 0;

            @Override
            public boolean hasNext() {
                return true; // Бесконечный поток
            }

            @Override
            public Integer next() {
                i = (i + 1) % MOD;
                j = (j + S[i]) % MOD;
                int temp = S[i];
                S[i] = S[j];
                S[j] = temp;
                return S[(S[i] + S[j]) % MOD];
            }
        };
    }

    /**
     * @brief Генерирует поток ключей для шифрования/дешифрования.
     * @param key - Ключ шифрования в виде массива байт.
     * @return Iterable, генерирующий поток ключей.
     *
     * Этот метод объединяет KSA и PRGA для генерации потока ключей.
     */
    public static Iterable<Integer> getKeystream(byte[] key) {
        int[] S = KSA(key);
        return PRGA(S);
    }

    /**
     * @brief Шифрует или дешифрует входной текст с использованием алгоритма RC4.
     * @param key - Ключ шифрования в виде массива байт.
     * @param text - Текст для шифрования/дешифрования в виде массива байт.
     * @return Зашифрованный или расшифрованный текст в виде шестнадцатеричной строки.
     *
     * Этот метод выполняет основную логику шифрования/дешифрования.
     */
    public static String encryptLogic(byte[] key, byte[] text) {
        Iterable<Integer> keystream = getKeystream(key);
        java.util.Iterator<Integer> iterator = keystream.iterator();
        StringBuilder res = new StringBuilder();
        for (byte c : text) {
            int val = (c ^ iterator.next()) & 0xFF;
            res.append(String.format("%02X", val));
        }
        return res.toString();
    }

    /**
     * @brief Шифрует текст с использованием алгоритма RC4.
     * @param key - Ключ шифрования в виде строки.
     * @param plaintext - Открытый текст для шифрования.
     * @return Зашифрованный текст в виде шестнадцатеричной строки.
     */
    public static String encrypt(String key, String plaintext) {
        byte[] keyBytes = key.getBytes();
        byte[] plaintextBytes = plaintext.getBytes();
        return encryptLogic(keyBytes, plaintextBytes);
    }

    /**
     * @brief Дешифрует зашифрованный текст с использованием алгоритма RC4.
     * @param key - Ключ шифрования в виде строки.
     * @param ciphertext - Зашифрованный текст для дешифрования.
     * @return Расшифрованный текст в виде строки.
     */
    public static String decrypt(String key, String ciphertext) {
        byte[] keyBytes = key.getBytes();
        byte[] ciphertextBytes = new byte[ciphertext.length() / 2];
        for (int i = 0; i < ciphertext.length(); i += 2) {
            ciphertextBytes[i / 2] = (byte) Integer.parseInt(ciphertext.substring(i, i + 2), 16);
        }
        String decryptedHex = encryptLogic(keyBytes, ciphertextBytes);
        byte[] decryptedBytes = new byte[decryptedHex.length() / 2];
        for (int i = 0; i < decryptedHex.length(); i += 2) {
            decryptedBytes[i / 2] = (byte) Integer.parseInt(decryptedHex.substring(i, i + 2), 16);
        }
        return new String(decryptedBytes);
    }

    /**
     * @brief Основной метод для тестирования шифрования и дешифрования RC4.
     * @param args - Аргументы командной строки (не используются).
     */
    public static void main(String[] args) {
        String key = "not-so-random-key";
        String plaintext = "Chu pa pi";
        String ciphertext = encrypt(key, plaintext);
        System.out.println("ciphertext: " + ciphertext);
    }
}
