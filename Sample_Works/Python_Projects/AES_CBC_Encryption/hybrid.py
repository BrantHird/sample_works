from Crypto.PublicKey import RSA 
from Crypto.Cipher import AES
from Crypto.Random import get_random_bytes
from Crypto.Cipher import AES, PKCS1_OAEP

class Principal:
    # key_length: RSA key length this principal will use
    # name: name of principal, save key under "name".der in DER format
    def __init__(self, key_length, name):
        self.name = name
        self.key_length = key_length
        self.own_key = self.create_rsa_key(self.key_length)
        with open("{}.der".format(name), "wb") as out_fh:
            out_fh.write(self.own_key.exportKey(format ='DER', pkcs=1))

    # Create RSA key of given key_length
    def create_rsa_key(self, key_length):
        rsa_keypair = RSA.generate(key_length)
        return rsa_keypair

    # Return public key part of public/private key pair
    def get_public_key(self):
        public_key = self.own_key.publickey()
        return public_key

    # Receiving means reading an hybrid-encrypted message from a file.
    # Returns: encrypted key (bytes), encrypted message (bytes), IV (bytes),
    # number of padding bytes
    def receive(self, filename):
        # YOUR TASK STARTS HERE
        with open(filename, "r") as message:
            ck_hex, cm_hex, iv_hex, pad_len_int = message.readlines()
        ck_bytes = bytes.fromhex(ck_hex)
        cm_bytes = bytes.fromhex(cm_hex)
        iv_bytes = bytes.fromhex(iv_hex)
        return [ck_bytes, cm_bytes, iv_bytes, pad_len_int]

    # Sending means writing an encrypted message plus metadata to a file.
    # Line 1: RSA-encrypted symmetric key, as hex string.
    # Line 2: Symmetrically encrypted message, as hex string.
    # Line 3: IV as hex string
    # Line 4: Number of padding bytes (string of int)
    def send(self, filename, msg):
        # YOUR TASK STARTS HERE
        with open(filename, "w") as message:
            counter =0 
            for line in msg:
                if(counter == 3):
                    output = line 
                else:
                    #print(len(line))
                    output = line.hex()
                message.write(str(output))
                message.write("\n")
                counter+=1
        pass

# Hybrid Cipher encapsulates the functionality of a hybrid cipher using
# RSA and AES-CBC.
# Key length of AES is a parameter.
class HybridCipher:

    # length_sym: length of symmetric key. Must be 128, 192, or 256.
    # own_key: public/private key pair of owner (principal who can decrypt)
    # remote_pub_key: public key of principal this hybrid cipher is encrypting to
    def __init__(self, length_sym, own_key, remote_pub_key):
        self.length_sym = length_sym
        self.own_key = own_key
        self.remote_pub_key = remote_pub_key
        self.cipher = self.create_aes_cipher(self.length_sym)
        pass


    # Creates an AES cipher in CBC mode with random IV, and random key
    # Returns: cipher, IV, symmetric key
    def create_aes_cipher(self, length):
        sym_len = get_random_bytes(length)
        cipher_rsa = PKCS1_OAEP.new(self.remote_pub_key)
        sym_key = cipher_rsa.encrypt(sym_len)
        cipher = AES.new(sym_len, AES.MODE_CBC)
        iv = cipher.IV
        return cipher, iv, sym_key


    # Decrypted hybrid-encrypted msg
    # Returns: decrypted message with padding removed, as string
    def decrypt(self, msg):
        cipher_rsa = PKCS1_OAEP.new(self.own_key)
        session_key = cipher_rsa.decrypt(msg[0])
        cipher_aes = AES.new(session_key, AES.MODE_CBC, msg[2])
        msg_dec = cipher_aes.decrypt(msg[1])
        rcvd_msg_dec_binary = self.strip_pad(msg_dec,msg[3])
        rcvd_msg_dec = rcvd_msg_dec_binary.decode()
        return rcvd_msg_dec


    # Encrypts plaintext message to encrypt in hybrid fashion.
    # Returns: encrypted symmetric key, encrypted message, IV, number of padding bytes
    def encrypt(self, msg):
        ck = self.cipher[2]
        padded_info = self.pad(msg)
        to_encrypt = padded_info[0]
        pad_len = padded_info[1]
        iv = self.cipher[1]
        cm = self.cipher[0].encrypt(to_encrypt.encode())
        return [ck, cm, iv, pad_len]

    # Padding for AES-CBC.
    # Pad up to multiple of block length by adding 0s (as byte)
    # Returns: padded message, number of padding bytes
    def pad(self, msg):
        num_pads = 0
        #print(len(msg))
        iterations = len(msg)%16
        for i in range(16-iterations):
            msg += '0'
            num_pads += 1
        #print(str(len(msg)%16))
        padded_msg = [msg,num_pads]
        return padded_msg

    # Strips padding and converts message to str.
    def strip_pad(self, msg, pad_len_int):
        msg_unpadded = msg[:len(msg) - int(pad_len_int,10)]
        return msg_unpadded




def main():
    # We create Alice as a principal. In this example, we choose a
    # 2048 bit RSA key.
    alice = Principal(2048, "alice")
    # We create Bob as a principal.
    bob = Principal(2048, "bob")

    # We create a HybridCipher for Alice to use. She uses Bob's public key
    # because he is the receiver. Her own public/private key pair goes in there, too,
    # for completeness.
    a_hybrid_cipher = HybridCipher(32, alice.own_key, bob.get_public_key())

    # Alice has a message for Bob.
    msg = "Hi Bob, it's Alice."
    # Alice uses the hybrid cipher to encrypt to Bob.
    msg_enc = a_hybrid_cipher.encrypt(msg)
    alice.send("msg.enc", msg_enc)
    #print(msg_enc)
    # Bob receives
    rcv_msg_enc = bob.receive("msg.enc")
    # Bob creates a HybridCipher. He configures it with his own public/private
    # key pair, and Alice's public key for completeness.
    b_hybrid_cipher = HybridCipher(16,bob.own_key,alice.get_public_key())
    # Bob decrypts.
    dec_msg = b_hybrid_cipher.decrypt(rcv_msg_enc)
    print(dec_msg)
    
    if msg == dec_msg:
       print("This worked!")

main()
