//
//  UrlImage.swift
//  Medico
//
//  Created by Dasha Gurinovich on 5.01.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import Combine

struct UrlImage: View {
    @ObservedObject var imageLoader: ImageLoader
    
    @State var image: UIImage
    
    init(withURL url: String,
         withDefaultImageName defaultImageName: String) {
        imageLoader = ImageLoader(urlString: url)
        
        self._image = State(initialValue: UIImage(named: defaultImageName) ?? UIImage())
    }

    var body: some View {
        Image(uiImage: image)
            .resizable()
            .aspectRatio(contentMode: .fit)
            .onReceive(imageLoader.didChange) { data in
                guard !data.isEmpty,
                      let image = UIImage(data: data) else { return }
                
                self.image = image
            }
    }
}

class ImageLoader: ObservableObject {
    var didChange = PassthroughSubject<Data, Never>()
    var data = Data() {
        didSet {
            didChange.send(data)
        }
    }

    init(urlString: String) {
        guard let url = URL(string: urlString) else { return }
        let task = URLSession.shared.dataTask(with: url) { data, response, error in
            guard let data = data else { return }
            DispatchQueue.main.async {
                self.data = data
            }
        }
        task.resume()
    }
}
