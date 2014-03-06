class MessagesController < ApplicationController

  def index
    username = params['user'] + '@localhost'
    @messages = Message.history(username).map do |message| 
      Message.hash_in_android(message)
    end

    @messages = Kaminari.paginate_array(@messages).page(params[:page])


    respond_to do |format|
      format.html
      if !@messages.nil?
        # @messages = @messages.map { |message| Message.hash_in_android(message) }

        format.json {render :json => {:messages => @messages}}
      end
    end
  end

end
