#$ ->
#  $.get "/persons", (persons) ->
#    $.each persons, (index, person) ->
#      $("#persons").append $("<li>").text person.name
#


$ ->
  ws = new WebSocket $("body").data("ws-url")
  ws.onmessage = (event) ->
    message = JSON.parse event.data
    switch message.type
      when "chathistory"
        populateChatHistory(message)
      when "chatupdate"
        updateChat(message)
      else
        console.log(message)


populateChatHistory = (message) ->
  console.log(message)

updateChat = (message) ->
  console.log(message)