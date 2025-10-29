import { TestBed } from '@angular/core/testing';

import { AdminWoroutService } from './admin-worout.service';

describe('AdminWoroutService', () => {
  let service: AdminWoroutService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdminWoroutService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
