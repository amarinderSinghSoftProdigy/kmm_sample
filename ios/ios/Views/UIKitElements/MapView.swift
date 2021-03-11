//
//  MapView.swift
//  Medico
//
//  Created by Dasha Gurinovich on 16.01.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import MapKit
import SwiftUI

struct MapView: UIViewRepresentable {
    let latitude: Double
    let longitude: Double
    
    func makeUIView(context: Context) -> MKMapView {
        let mapView = MKMapView()
        mapView.delegate = context.coordinator
        
        setAnnotation(for: mapView)
        
        return mapView
    }

    func updateUIView(_ view: MKMapView, context: Context) { }
    
    private func setAnnotation(for mapView: MKMapView) {
        let location = MKPointAnnotation()
        location.coordinate = CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
        
        let locationDistance = CLLocationDistance(exactly: 1000)!
        
        let region = MKCoordinateRegion(center: location.coordinate,
                                        latitudinalMeters: locationDistance,
                                        longitudinalMeters: locationDistance)
        mapView.setRegion(mapView.regionThatFits(region), animated: false)
        
        mapView.addAnnotation(location)
    }
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }

    class Coordinator: NSObject, MKMapViewDelegate {
        var parent: MapView

        init(_ parent: MapView) {
            self.parent = parent
        }
        
        func mapView(_ mapView: MKMapView, viewFor annotation: MKAnnotation) -> MKAnnotationView? {
            guard !annotation.isKind(of: MKUserLocation.self) else { return nil }

            let annotationIdentifier = "AnnotationIdentifier"

            var annotationView = mapView.dequeueReusableAnnotationView(withIdentifier: annotationIdentifier)
            
            if annotationView == nil {
                annotationView = MKAnnotationView(annotation: annotation, reuseIdentifier: annotationIdentifier)
                annotationView?.canShowCallout = true
            }
            else {
                annotationView?.annotation = annotation
            }
            
            annotationView?.image = UIImage(named: "MapPin")?
                .colorized(color: UIColor(named: "LightBlue"))

            return annotationView

        }
    }
}
