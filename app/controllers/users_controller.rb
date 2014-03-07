require "net/http"
require "uri"

class UsersController < ApplicationController
  before_filter :pre_load

  def pre_load
    @user = User.find(params[:id]) if params[:id]
  end

  def index
    @users = User.all
    @user = User.new
  end

  def show
  end

  def create
    
    @user = User.new(user_params)
    
    if @user.save
      uri = URI.parse("http://localhost:9090/plugins/userService/userservice?type=add&secret=P5LyXbLl&username=#{@user.username}&password=#{@user.password}")


      http = Net::HTTP.new(uri.host, uri.port)
      response = http.request(Net::HTTP::Get.new(uri.request_uri))

      p response.body
      p response.code
    end

    redirect_to "/users"
  end


  def disable
    uri = URI.parse("http://localhost:9090/plugins/userService/userservice?type=disable&secret=P5LyXbLl&username=student1")

    http = Net::HTTP.new(uri.host, uri.port)
    response = http.request(Net::HTTP::Get.new(uri.request_uri))

    p response.body
    p response.code

    render :nothing => true
  end


  def enable
    uri = URI.parse("http://localhost:9090/plugins/userService/userservice?type=enable&secret=P5LyXbLl&username=student1")

    http = Net::HTTP.new(uri.host, uri.port)
    response = http.request(Net::HTTP::Get.new(uri.request_uri))

    p response.body
    p response.code

    render :nothing => true
  end


  def destroy
    if @user.destroy

      uri = URI.parse("http://localhost:9090/plugins/userService/userservice?type=delete&secret=P5LyXbLl&username=student1")

      http = Net::HTTP.new(uri.host, uri.port)
      response = http.request(Net::HTTP::Get.new(uri.request_uri))

      p response.body
      p response.code
    end

    redirect_to "/users"
  end


  def update
    if @user.update_attributes(user_params)
      p @user.password
      p @user.username
      uri = URI.parse("http://localhost:9090/plugins/userService/userservice?type=update&secret=P5LyXbLl&username=#{@user.username}&password=#{@user.password}")

      http = Net::HTTP.new(uri.host, uri.port)
      response = http.request(Net::HTTP::Get.new(uri.request_uri))

      p response.body
      p response.code
    end

    redirect_to "/users/#{@user.id}"
  end



  def login
    if User.where(:email => user_params[:email], :password => user_params[:password]).exists?
    # if User.where(:username => user_params[:username], :password => user_params[:password]).exists?
      id = User.where(:email => user_params[:email], :password => user_params[:password]).first.id
      render :json => {:id => id}, :status => 200

    else
      render :nothing => true, :status => 401

    end
  end


  def user_params
    params[:user]
  end

 
end
