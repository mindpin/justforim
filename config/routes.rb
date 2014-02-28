Justforim::Application.routes.draw do
  resources :users do
    collection do
      post :login
      post :disable
      post :enable
    end
  end
end
