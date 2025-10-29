import { TestBed } from '@angular/core/testing';

import { AdminDashBoardService } from './admin-dash-board.service';

describe('AdminDashBoardService', () => {
  let service: AdminDashBoardService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdminDashBoardService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
