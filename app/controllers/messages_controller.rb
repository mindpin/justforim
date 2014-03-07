class MessagesController < ApplicationController

  def index
    username = params['user'] + '@localhost'

    @messages = Kaminari.paginate_array(OfMessageArchive.where("fromJID = ? or toJID = ?", username, username)).page(params[:page])

    @messages = @messages.each do |message| 
      OfMessageArchive.hash_in_android(message)
    end


    respond_to do |format|
      format.html
      if !@messages.nil?
        # @messages = @messages.map { |message| Message.hash_in_android(message) }

        format.json {render :json => {:messages => @messages}}
      end
    end
  end

end
