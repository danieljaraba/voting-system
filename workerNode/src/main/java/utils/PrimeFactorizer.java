package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PrimeFactorizer {

    private List<Integer> primes;

    public PrimeFactorizer() throws IOException {
        primes = new ArrayList<>();
        loadPrimesFromCache("primes_cache.txt");
    }

    private void loadPrimesFromCache(String cacheFile) throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream(cacheFile);
        if (is == null) {
            throw new IOException("Archivo de caché no encontrado: " + cacheFile);
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                primes.add(Integer.parseInt(line));
            }
        }
        System.out.println("Números primos cargados desde el archivo: " + primes.size());
    }

    public List<Long> getPrimeFactors(long n) {
        List<Long> factors = new ArrayList<>();
        factorizeRecursive(n, factors, -1L);
        return factors;
    }

    private void factorizeRecursive(long n, List<Long> factors, long lastFactor) {
        if (n <= 1) {
            return;
        }
        for (int prime : primes) {
            if ((long) prime * prime > n) {
                break;
            }
            if (n % prime == 0) {
                if (lastFactor != prime) {
                    factors.add((long) prime);
                }
                while (n % prime == 0) {
                    n /= prime;
                }
                lastFactor = prime;
            }
        }
        if (n == 1) {
            return;
        }
        if (isPrime(n)) {
            if (lastFactor != n) {
                factors.add(n);
            }
            return;
        }
        long factor = pollardsRho(n);
        if (factor == n) {
            if (lastFactor != n) {
                factors.add(n);
            }
            return;
        }
        factorizeRecursive(factor, factors, lastFactor);
        factorizeRecursive(n / factor, factors, factor);
    }

    private boolean isPrime(long n) {
        if (n < 2)
            return false;
        int[] bases = { 2, 3, 5, 7, 11 };
        long d = n - 1;
        int s = 0;
        while ((d & 1) == 0) {
            d >>= 1;
            s++;
        }
        outer: for (int a : bases) {
            if (a >= n)
                continue;
            long ad = modPow(a, d, n);
            if (ad == 1 || ad == n - 1)
                continue;
            for (int r = 1; r < s; r++) {
                ad = modMul(ad, ad, n);
                if (ad == n - 1)
                    continue outer;
            }
            return false;
        }
        return true;
    }

    private long pollardsRho(long n) {
        if (n % 2 == 0)
            return 2;
        Random rand = new Random();
        long x = rand.nextInt((int) n - 2) + 2;
        long y = x;
        long c = rand.nextInt((int) n - 1) + 1;
        long d = 1;
        while (d == 1) {
            x = (modMul(x, x, n) + c) % n;
            y = (modMul(y, y, n) + c) % n;
            y = (modMul(y, y, n) + c) % n;
            d = gcd(Math.abs(x - y), n);
            if (d == n)
                return pollardsRho(n);
        }
        return d;
    }

    private long modMul(long a, long b, long mod) {
        return BigInteger.valueOf(a).multiply(BigInteger.valueOf(b)).mod(BigInteger.valueOf(mod)).longValue();
    }

    private long modPow(long base, long exp, long mod) {
        long result = 1;
        base = base % mod;
        while (exp > 0) {
            if ((exp & 1) == 1)
                result = modMul(result, base, mod);
            exp >>= 1;
            base = modMul(base, base, mod);
        }
        return result;
    }

    private long gcd(long a, long b) {
        while (b != 0) {
            long tmp = a % b;
            a = b;
            b = tmp;
        }
        return a;
    }
}
