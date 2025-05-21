import request from '@/utils/request'


export function sendCode(email) {
  return request({
    url: `http://localhost:8080/email/sendver?email=${email}`, // This URL might be from an old API
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post'
  })
}
