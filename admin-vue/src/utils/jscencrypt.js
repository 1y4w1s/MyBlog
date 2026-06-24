import JSEncrypt from 'jsencrypt'

// RSA公钥
const publicKey = 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDfY7Z0T4M+8s+0'

export function encrypt(text) {
  const encryptor = new JSEncrypt()
  encryptor.setPublicKey(publicKey)
  return encryptor.encrypt(text)
}

export function decrypt(text) {
  const decryptor = new JSEncrypt()
  decryptor.setPrivateKey(publicKey)
  return decryptor.decrypt(text)
}

export default {
  encrypt,
  decrypt
}
