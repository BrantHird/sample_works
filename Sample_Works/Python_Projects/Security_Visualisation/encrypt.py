from Crypto.Cipher import AES

def pad(contents):

    if(len(contents)%16!=0):
        for i in range(16 - len(contents)%16):
            contents += bytes([0])
    return contents

#Create the given key
key = "INFO3616INFO3616"

#create the cipher
cipher = AES.new(key,AES.MODE_ECB)

#open the png file
image = open("myfile.png.bin","rb")
#retrieve image image_contents
image_contents = image.read()
#pad the image contents
padded_contents = pad(image_contents)

#encrypt the message
encrypted_image = cipher.encrypt(padded_contents)

#write the encryption to output file.
output = open("myfile.png.bin.enc.bin","wb")
output.write(encrypted_image)
