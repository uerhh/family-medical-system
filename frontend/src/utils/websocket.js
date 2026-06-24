let ws = null
let reconnectTimer = null
let reconnectAttempts = 0
let messageCallback = null
let currentToken = null

export function connectWs(token, onMessage) {
  currentToken = token
  messageCallback = onMessage
  const wsUrl = `ws://${window.location.hostname}:5173/ws/chat?token=${token}`
  ws = new WebSocket(wsUrl)

  ws.onopen = () => {
    reconnectAttempts = 0
    console.log('WebSocket connected')
  }

  ws.onmessage = (event) => {
    try {
      const data = JSON.parse(event.data)
      if (messageCallback) messageCallback(data)
    } catch (e) {
      console.error('Parse WebSocket message error:', e)
    }
  }

  ws.onclose = () => {
    if (currentToken) {
      const delay = Math.min(1000 * Math.pow(2, reconnectAttempts), 30000)
      reconnectAttempts++
      reconnectTimer = setTimeout(() => connectWs(currentToken, messageCallback), delay)
    }
  }

  ws.onerror = (err) => {
    console.error('WebSocket error:', err)
    ws.close()
  }
}

export function sendWsMessage(data) {
  if (ws && ws.readyState === WebSocket.OPEN) {
    ws.send(JSON.stringify(data))
    return true
  }
  return false
}

export function disconnectWs() {
  currentToken = null
  messageCallback = null
  if (reconnectTimer) {
    clearTimeout(reconnectTimer)
    reconnectTimer = null
  }
  if (ws) {
    ws.close()
    ws = null
  }
}
