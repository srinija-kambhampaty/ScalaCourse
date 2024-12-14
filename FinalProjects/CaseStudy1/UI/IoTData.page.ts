import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RestService } from 'src/app/services/rest.service';

@Component({
  selector: 'app-sensors',
  templateUrl: './IoTData.page.html',
  styleUrls: ['./IoTData.page.scss'],
})
export class InventoryPage implements OnInit {

  sensors: any;
  filteredSensors : any;
  searchTerm = '';
  inventoryData: any;
  constructor(private restService: RestService, private router: Router) { }

  async ngOnInit(): Promise<void> {
    try {
      this.sensors = await this.restService.getSensors();
      this.filteredSensors = this.sensors;
    } catch (error) {
      console.error('Error loading data', error);
    }
  }

  filterSensors() {
    const term = this.searchTerm.toLowerCase();
    this.filteredSensors = this.sensors.filter((sensor: any) =>
      sensor.sensorId.toLowerCase().includes(term)
    );
  }
}
