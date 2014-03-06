class OfMessageArchive < ActiveRecord::Base
  establish_connection("openfire_development")

  # def self.history(username)
    
  #   sql = "select * from ofMessageArchive where fromJID = '#{username}' or toJID = '#{username}' "

  #   connection.execute(sql)
  # end

  def self.hash_in_android(message)
    return {
      'fromJID'  => message[1],
      'toJID' => message[3],
      'body'      => message[6],
      'sentDate' => message[5]
    }

  end

end
