import os
import sys
import binascii
# from Crypto.Cipher import AES

plain1 = "################".encode()
plain2 = "#              #".encode()
plain3 = "#    START     #".encode()
plainX = "#     END      #".encode()
plainStart = [plain1, plain2, plain3, plain2, plain1]
plainEnd = [plain1, plain2, plainX, plain2, plain1]

plain_text = [plain1,plain2,plain3,plain2,plain1,plain1, plain2, plainX, plain2, plain1]


# key = ... # I am not about to tell you!
#
# cipher = AES.new(key, AES.MODE_ECB)


def run_xor(b1, b2):
    if len(b1) != len(b2):
        print("XOR: mismatching length of byte arrays")
        # os.exit(-1)

    output = []

    for i in range(0, len(b1)):
        x = b1[i] ^ b2[i]
        t = "%x" % x
        if len(t) == 1:
            t = "0" + t
        output.append(t)
    return "".join(output)

#
# def transcrypt(nonce, input_text):
#
#     enc_nonce = cipher.encrypt(nonce)
#     ciphertext = run_xor(enc_nonce, input_text)
#     return ciphertext
#
#
#
# def encrypt_input_file(filename):
#     with open(filename, "r") as infh, open("encrypted.enc", "w") as outfh:
#         i = 0
#         for line in infh:
#             line = line.rstrip("\n")
#             nonce = "000000000000000" + str(i)
#             res = transcrypt(nonce.encode(), line.encode())
#             outfh.write(str(i) + "," + res + "\n")
#             i = (i + 1) % 10









def break_input_file(filename):
    # YOUR JOB STARTS HERE
    encrypted_text = open(filename,"r")

    #Array to store all blocks according
    #to the index number
    blocks = [[] for x in range(10)]

    #For loop to access each line of the encrypted text
    for line in encrypted_text:
        #Access only the hexadecimal string
        block = line[2:]
        #Add to array according to index number
        blocks[int(line[0])].append(block)


    #Generate an array holding all characters. These characters are in the
    #form of 16byte strings eg ("aaaaaaaaaaaaaaaa") and are used to determine
    #the corresponding hexvalues for each type of character using the encrypted nonce
    letters = []
    letters_file = open("letters.txt","r")
    for line in letters_file:
        line = line.rstrip("\n")
        letters.append(line)

    index_dict = []


    #for loop to deduce the encrypted nonce for each index
    for index in range(len(blocks)):

        #row number to get the encrypted output for the plaintext given
        row = 0

        if(index>4):
            row = len(blocks[index]) - 1


        #get rid of new line character in the hexadecimal string
        check = blocks[index][row].rstrip("\n")


        #Convert hexadecimal string to bytes object
        bytes_object = bytes.fromhex(check)

        #Run xor with bytes object and corresponding plain text to
        #get the encrypted nonce for the particular index.

        res = run_xor(plain_text[index],bytes_object)

        #Get the byte form of the encrypted nonce.
        res_bytes = bytes.fromhex(res)

        #make an array that will hold all encryption for every character
        index_section = [[] for x in range(len(letters))]

        #for every character
        for i in range(len(letters)):
            #Apply xor with the encrypted nonce and a specific character to obtain
            #the corresponding hexvalues for the character.

            results = run_xor((res_bytes),letters[i].encode())

            #Get the byte form of the obtained hexvalues
            bytes_object = bytes.fromhex(results)

            #add it to the array holding all the encrypted outputs for each character
            index_section[i] = bytes_object

        #append to array which holds the index_section for each index.
        index_dict.append(index_section)

    #Go back to beginning of encrypted file
    encrypted_text.seek(0,0)

    #for every line in the encrypted text
    for line in encrypted_text:
        #Access only the hexadecimal string
        pre_decrypt = line[2:]
        #get the index
        index = int(line[0],10)
        #Get the bytes of the pre-decrypted data.
        pre_decrypt_bytes = bytes.fromhex(pre_decrypt)
        #Create an array for the decrypted output
        decrypted_line = [" "]*len(pre_decrypt_bytes)
        #For every byte in the predecrypted message
        for i in range(len(pre_decrypt_bytes)):
                #For every character
                for j in range(len(index_dict[index])):
                    #Check whether the output of the character matches the predecrypted byte
                    if index_dict[index][j][i] == pre_decrypt_bytes[i]:
                        #if so add it as a character of the decrypted line.
                        decrypted_line[i] = letters[j][0]
                        break

        print("".join(decrypted_line))

    # YOUR JOB ENDS HERE
    pass


def main(args):
    if len(args) > 1:
        filename = args[1]
        break_input_file(filename)
    else:
        print("Please provide an file to break!")

if __name__ == '__main__':
    main(sys.argv)
