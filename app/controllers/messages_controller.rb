class MessagesController < ApplicationController

  def index
    username = params['user'] + '@localhost'
    @messages = OfMessageArchive.history(username).map do |message| 
      OfMessageArchive.hash_in_android(message)
    end

    @messages = Kaminari.paginate_array(@messages).page(params[:page])
    OfMessageArchive.where("fromJID = ? or toJID = ?", params[:orders], false).page(params[:page]


    respond_to do |format|
      format.html
      if !@messages.nil?
        # @messages = @messages.map { |message| Message.hash_in_android(message) }

        format.json {render :json => {:messages => @messages}}
      end
    end
  end

end
