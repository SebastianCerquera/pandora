#include <iostream>
#include <stdint.h>
#include <gmp.h>
#include <thread>
#include <math.h>                   // pow()
#include <cuda_runtime.h>           // cudaFreeHost()
#include "CUDASieve/cudasieve.hpp"  // CudaSieve::getHostPrimes()                   
 
void checkModulus(uint64_t * primes, mpz_t base, size_t lenMin, size_t lenMax)
{
    mpz_t prime, mod;
    mpz_inits(mod, prime, NULL);
 
    for(uint32_t i = lenMin; i < lenMax; i++){
        mpz_set_ui(prime, primes[i]);
        mpz_mod (mod, base, prime);
 
        if(mpz_cmp_ui(mod, 0) == 0){
            std::cout << "SOLUTION FOUND: ";
            mpz_out_str(stdout, 10, prime);
            std::cout << "\n";
        }
    }
}
 
int main(int argc, char* argv[])
{
    mpz_t result, base;
    mpz_inits(result, base, NULL);
    mpz_set_str(base, argv[3], 10);
 
    std::size_t x;
    uint64_t bottom = std::stoul(argv[1], &x, 10);
    uint64_t top = std::stoul(argv[2], &x, 10);
 
    size_t len;
 
    uint64_t * primes = CudaSieve::getHostPrimes(bottom, top, len);
    size_t interval = (len - (len % 4))/4;
 
    std::thread t1(checkModulus, primes, base, 0*interval, 1*interval);
    std::thread t2(checkModulus, primes, base, 1*interval, 2*interval);
    std::thread t3(checkModulus, primes, base, 2*interval, 3*interval);
    std::thread t4(checkModulus, primes, base, 3*interval, 4*interval + (len % 4));
 
    t1.join();
    t2.join();
    t3.join();
    t4.join();
    
    cudaFreeHost(primes);            // must be freed with this call b/c page-locked memory is used.
    return 0;
}
