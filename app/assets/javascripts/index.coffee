$ ->
  $("#chatText").focus()

  ws = new WebSocket $("body").data("ws-url")
  ws.onmessage = (event) ->
    message = JSON.parse event.data
    switch message.type
      when "chathistory"
        populateChatHistory(message)
      when "chatmessage"
        updateChat(message)
      when "userlist"
        updateUserList(message)
      when "setnickname"
        setNickname(message)
      else
        console.log(message)

  $("#sendForm").submit (event) ->
    event.preventDefault()
    jsonMessage = {roomId: 0, sender: "Me", message: $("#chatText").val()}
    msg = JSON.stringify(jsonMessage)
    console.log("Sending message " + msg)
    updateChat(jsonMessage)
    ws.send(msg)
    $("#chatText").val("")

populateChatHistory = (message) ->
  console.log(message)

updateChat = (message) ->
  msg = $("<span>").addClass("message").text(message.message)
  sender = $("<span>").addClass("sender").text(message.sender + ": ")
  msgContainer = $("<div>").addClass("messageContainer").append(sender).append(msg)
  $("#mainChat").append(msgContainer)
  $("#mainChat").prop("scrollTop", $("#mainChat").prop("scrollHeight"))

updateUserList = (message) ->
  $("#userContainer").empty()
  $("#userContainer").append(
    $("<div>").addClass("userName").toggleClass("myNickname", isItMe(user)).text(user)
  ) for user in message.users

setNickname = (message) ->
  window.myNickname = message.nickname
  console.log("set window.myNickname: " + window.myNickname)

isItMe = (nickname) ->
  console.log("window.myNickname: " + window.myNickname)
  return window.myNickname == nickname
