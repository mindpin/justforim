class MessagesController < ApplicationController

  def index
    username = params['user'] + '@localhost'
    @messages = Message.history(username).map { |message| Message.hash_in_android(message) }


    respond_to do |format|
      format.html
      if !@messages.nil?
        # @messages = @messages.map { |message| Message.hash_in_android(message) }

        format.json {render :json => {:messages => @messages}}
      end
    end
  end

end
